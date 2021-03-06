package org.pm4j.core.pm.impl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.pm4j.core.pm.PmConversation;
import org.pm4j.core.pm.PmDataInput;
import org.pm4j.core.pm.PmEvent;
import org.pm4j.core.pm.PmEvent.ValueChangeKind;
import org.pm4j.core.pm.PmEventListener;
import org.pm4j.core.pm.PmObject;
import org.pm4j.core.pm.api.PmVisitorApi;
import org.pm4j.core.pm.api.PmVisitorApi.PmVisitCallBack;
import org.pm4j.core.pm.api.PmVisitorApi.PmVisitHint;
import org.pm4j.core.pm.api.PmVisitorApi.PmVisitResult;

/**
 * Generates an event for each item within a PM tree.
 * <p>
 * Supports {@link PmEventListener.WithPreprocessCallback} as well as {@link PmEventListener.PostProcessor}s.
 * <p>
 * LIMITATION: Currently pre process events are not sent to dynamic child PMs. That means that they will not be sent to
 * table row PMs.<br>
 * Reason for that limitation: The event may cause (of inform about) a changed data constellation.
 * In this scenario it is not ensured that a dynamic (factory generated) PM that was found in the
 * pre-processing step will still exist in the post-processing step.
 * <p>
 * A future extension may add some different handling. To preserve behaviour compatibility, a changed behaviour
 * must be configured explicitely.
 *
 * @author olaf boede
 */
public class BroadcastPmEventProcessor {
  protected final PmDataInput rootPm;
  private final int eventMask;
  private final ValueChangeKind changeKind;
  private final Map<PmObject, PmEvent> pmToEventMap = new IdentityHashMap<PmObject, PmEvent>();
  private final List<PmEvent> eventsToPostProcess = new ArrayList<PmEvent>();

  public BroadcastPmEventProcessor(PmDataInput rootPm, final int eventMask) {
    this(rootPm, eventMask, ValueChangeKind.UNKNOWN);
  }

  public BroadcastPmEventProcessor(PmDataInput rootPm, final int eventMask, final ValueChangeKind changeKind) {
    this.rootPm = rootPm;
    this.eventMask = eventMask;
    this.changeKind = changeKind;
  }

  public void doIt() {
    preProcess();
    fireEvents();
    postProcess();
  }

  protected void preProcess() {
    // The pre process listener is currently limited to inform only the not factory
    // generated PM's.
    //
    // The generated PM's can only get informed after they got generated by the main
    // event... - Thus it seems to be not possible to pre-process them at all.
    PmVisitorApi.visit(rootPm, new PmVisitCallBack() {
      @Override
      public PmVisitResult visit(PmObject pm) {
        // TODO olaf: make a VistHint.SKIP_INVISIBLE_CONVERSATION
        // Don't iterate over closed sub conversations (e.g. closed popup PMs).
        // This will only generate overhead an trouble.
        if ((pm instanceof PmConversation) && !pm.isPmVisible()) {
          return PmVisitResult.SKIP_CHILDREN;
        }

        PmEventApiHandler.sendToListeners(pm, getEventForPm(pm), true /* preProcess */);
        return PmVisitResult.CONTINUE;
      }
    },
    PmVisitHint.SKIP_FACTORY_GENERATED_CHILD_PMS);

  }

  protected void fireEvents() {
    // Main visitor loop: Each child gets the information about the 'change all' event.
    PmVisitCallBack handleEventCallBack = new PmVisitCallBack() {
      @Override
      public PmVisitResult visit(PmObject pm) {
        // Don't iterate over closed sub conversations (e.g. closed popup PMs).
        // This will only generate overhead an trouble.
        if ((pm instanceof PmConversation) && !pm.isPmVisible()) {
          return PmVisitResult.SKIP_CHILDREN;
        }

        // If there is an existing event instance from the pre-process loop, it will
        // be used again.
        // This way it is possible to accumulate the post processing requests.
        PmEvent e = getEventForPm(pm);

        PmEventApiHandler.firePmEvent(pm, e, false /* handle */);

        // If there is a registered post processor, remember this event for post
        // processing.
        // Example: A table wants to restore its selection after a reload of its backing bean.
        if (!e.getPostProcessorToPayloadMap().isEmpty()) {
          eventsToPostProcess.add(e);
        }
        return PmVisitResult.CONTINUE;
      }
    };

    // Fire the 'everything has changed' information to the whole sub tree.
    PmVisitorApi.visit(rootPm, handleEventCallBack, PmVisitHint.SKIP_NOT_INITIALIZED);
  }

  protected void postProcess() {
    // Perform the event postprocessing for all listeners that requested it.
    for (PmEvent e : eventsToPostProcess) {
      PmEventApiHandler.postProcessEvent(e);
    }

    PmEventApiHandler.propagateEventToParents(rootPm, getEventForPm(rootPm));
  }

  /**
   * Provides the event instance used for the given PM.
   * <p>
   * If there is an existing event instance from a previous event processing step, it will
   * be used again.
   * <p>
   * This way it is possible to accumulate PM related data within the event instance.
   *
   * @param pm
   * @return
   */
  protected final PmEvent getEventForPm(PmObject pm) {
    PmEvent event = pmToEventMap.get(pm);
    if (event == null) {
        event = new PmEvent(pm, eventMask, changeKind);
        pmToEventMap.put(pm, event);
    }
    return event;
  }
}