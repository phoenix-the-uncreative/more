package msifeed.mc.mellow.layout;

import msifeed.mc.mellow.widgets.Widget;

import java.util.Collection;

public abstract class Layout {
    public static final Layout NONE = new NoneLayout();

    public abstract void apply(Widget widget, Collection<Widget> children);

    private static final class NoneLayout extends Layout {
        @Override
        public void apply(Widget widget, Collection<Widget> children) {
        }
    }
}
