package org.neo4j.server.ext.visualization.gwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class NodeHandler implements MouseDownHandler, MouseUpHandler,
        MouseMoveHandler {
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;
    private VGraphComponent parent;
    private Widget node;

    public NodeHandler(Widget node, VGraphComponent parent) {
        this.node = node;
        this.parent = parent;
        node.addDomHandler(this, MouseDownEvent.getType());
        node.addDomHandler(this, MouseMoveEvent.getType());
        node.addDomHandler(this, MouseUpEvent.getType());
    }

    public void onMouseDown(MouseDownEvent event) {
        dragging = true;

        DOM.setCapture(node.getElement());
        dragStartX = event.getX();
        dragStartY = event.getY();
        event.preventDefault();
    }

    public void onMouseUp(MouseUpEvent event) {
        dragging = false;
        DOM.releaseCapture(node.getElement());
        event.preventDefault();
    }

    public void onMouseMove(MouseMoveEvent event) {
        if (dragging) {
            Element element = node.getElement();
            int newX = event.getX() + element.getOffsetLeft() - dragStartX;
            newX = limit(0, newX,
                    parent.getOffsetWidth() - node.getOffsetWidth());
            int newY = event.getY() + element.getOffsetTop() - dragStartY;
            newY = limit(0, newY,
                    parent.getOffsetHeight() - node.getOffsetHeight());
            Style style = element.getStyle();
            style.setLeft(newX, Unit.PX);
            style.setTop(newY, Unit.PX);
            parent.updateLinesFor(node);
            int clientX = event.getClientX();
            int clientY = event.getClientY();
            if (clientX < 0 || clientY < 0 || clientX > Window.getClientWidth()
                    || clientY > Window.getClientHeight()) {
                dragging = false;
            }
        }
        event.preventDefault();
    }

    private static int limit(int min, int value, int max) {
        return Math.min(Math.max(min, value), max);
    }
}
