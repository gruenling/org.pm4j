package org.pm4j.common.pageable.querybased.pagequery;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.pm4j.common.pageable.PageableCollectionTestBase.Bean;
import org.pm4j.common.pageable.querybased.pagequery.PageQueryCollectionTest.TestService;
import org.pm4j.common.query.QueryParams;

public class PageQueryCollectionCacheTest {

  private static int NUM_OF_ITEMS = 19;
  private TestService service = new PageQueryCollectionTest.TestService();


  @Before
  public void setUp() {
    for (int counter = 0; counter < NUM_OF_ITEMS; ++counter) {
      service.addBean(new Bean(counter, Integer.toString(counter)));
    }
  }

  @Test
  public void testFullCollection() {
    QueryParams qp = new QueryParams();
    PageQueryCollectionCache<Bean> cache = new PageQueryCollectionCache<Bean>(service, qp, 2);

    for (int i=0; i<NUM_OF_ITEMS; ++i) {
      assertEquals(i, cache.getAt(i).id.intValue());
    }
  }

}
