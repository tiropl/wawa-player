package com.wawa_player.android.tv.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class CoreTest {

    @Test
    public void objectFromReadsFieldsAndOptions() {
        Core core = Core.objectFrom("{\"auth\":\"auth\",\"name\":\"Name\",\"pass\":\"Pass\",\"broker\":\"broker\",\"domain\":\"Domain\",\"sign\":\"Sign\",\"pkg\":\"pkg\",\"so\":\"so\",\"option\":[{\"key\":\"mode\",\"values\":[\"a\",\"b\"]}]}");

        assertEquals("auth", core.getAuth());
        assertEquals("Name", core.getName());
        assertEquals("Pass", core.getPass());
        assertEquals("broker", core.getBroker());
        assertEquals("Domain", core.getDomain());
        assertEquals("Sign", core.getSign());
        assertEquals("pkg", core.getPkg());
        assertEquals("so", core.getSo());
        assertEquals(1, core.getOption().size());
        assertEquals("mode", core.getOption().get(0).getKey());
        assertEquals(2, core.getOption().get(0).getValues().size());
        assertEquals("a", core.getOption().get(0).getValues().get(0));
        assertTrue(core.getHook() != null);
    }

    @Test
    public void emptyFieldsAndOptionsAreSafe() {
        Core core = Core.objectFrom("{}");

        assertEquals("", core.getAuth());
        assertEquals("", core.getName());
        assertEquals("", core.getPass());
        assertEquals("", core.getBroker());
        assertEquals("", core.getDomain());
        assertEquals("", core.getResp());
        assertEquals("", core.getSign());
        assertEquals("", core.getPkg());
        assertEquals("", core.getSo());
        assertEquals(Collections.emptyList(), core.getOption());
        assertNull(core.getHook());
    }

    @Test
    public void equalsUsesResolvedSign() {
        Core first = Core.objectFrom("{\"sign\":\"same\"}");
        Core second = Core.objectFrom("{\"sign\":\"same\"}");
        Core other = Core.objectFrom("{\"sign\":\"other\"}");

        assertEquals(first, second);
        assertTrue(!first.equals(other));
    }
}
