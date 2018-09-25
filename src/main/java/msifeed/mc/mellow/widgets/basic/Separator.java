package msifeed.mc.mellow.widgets.basic;

import msifeed.mc.mellow.Mellow;
import msifeed.mc.mellow.render.RenderParts;
import msifeed.mc.mellow.render.RenderUtils;
import msifeed.mc.mellow.render.RenderWidgets;
import msifeed.mc.mellow.theme.Part;
import msifeed.mc.mellow.utils.Geom;
import msifeed.mc.mellow.utils.SizePolicy;
import msifeed.mc.mellow.widgets.Widget;

public class Separator extends Widget {
    protected Part horizontalPart = Mellow.THEME.parts.get("separator_hor");

    public Separator() {
        setSizeHint(horizontalPart.size.x, horizontalPart.size.y);
        setVerSizePolicy(SizePolicy.Policy.FIXED);
    }

    @Override
    protected void renderSelf() {
        final Geom geom = getGeometry();
        RenderWidgets.beginCropped(geom);
        final int endX = geom.x + geom.w;
        final int partWidth = horizontalPart.pos.x / RenderUtils.getScreenScaleFactor() + 1;
        for (int x = geom.x; x < endX; x += partWidth) {
            RenderParts.slice(horizontalPart, x, geom.y, geom.z);
        }
        RenderWidgets.endCropped();
    }
}