    	if (marker instanceof ValueMarker) {
    		ValueMarker vm = (ValueMarker) marker;
    		double value = vm.getValue();
    		Range range = valueAxis.getRange();
    		if (!range.contains(value)) {
    			return;
    		}

    		double v = valueAxis.valueToJava2D(value, dataArea,
    				plot.getRangeAxisEdge());
    		PlotOrientation orientation = plot.getOrientation();
    		Line2D line = null;
    		if (orientation == PlotOrientation.VERTICAL) {
    			line = new Line2D.Double(dataArea.getMinX(), v,
    					dataArea.getMaxX(), v);
    		}
    		else if (orientation == PlotOrientation.HORIZONTAL) {
    			line = new Line2D.Double(v, dataArea.getMinY(), v,
    					dataArea.getMaxY());
    		}

    		final Composite originalComposite = g2.getComposite();
    		g2.setComposite(AlphaComposite.getInstance(
    				AlphaComposite.SRC_OVER, marker.getAlpha()));
    		g2.setPaint(marker.getPaint());
    		g2.setStroke(marker.getStroke());
    		g2.draw(line);

    		String label = marker.getLabel();
    		RectangleAnchor anchor = marker.getLabelAnchor();
    		if (label != null) {
    			Font labelFont = marker.getLabelFont();
    			g2.setFont(labelFont);
    			g2.setPaint(marker.getLabelPaint());
    			Rectangle2D markerArea = line.getBounds2D();
    			RectangleInsets markerOffset = marker.getLabelOffset();
    			LengthAdjustmentType labelOffsetType = LengthAdjustmentType.EXPAND;
    			Rectangle2D anchorRect = null;
    			if (orientation == PlotOrientation.VERTICAL) {
    				anchorRect = markerOffset.createAdjustedRectangle(markerArea,
    						LengthAdjustmentType.CONTRACT, labelOffsetType);
    			}
    			else if (orientation == PlotOrientation.HORIZONTAL) {
    				anchorRect = markerOffset.createAdjustedRectangle(markerArea,
    						labelOffsetType, LengthAdjustmentType.CONTRACT);
    			}
    			Point2D coordinates = RectangleAnchor.coordinates(anchorRect, anchor);
    			TextUtilities.drawAlignedString(label, g2,
    					(float) coordinates.getX(), (float) coordinates.getY(),
    					marker.getLabelTextAnchor());
    		}
    		g2.setComposite(originalComposite);
    	}
    	else if (marker instanceof IntervalMarker) {
    		IntervalMarker im = (IntervalMarker) marker;
    		double start = im.getStartValue();
    		double end = im.getEndValue();
    		Range range = valueAxis.getRange();
    		if (!(range.intersects(start, end))) {
    			return;
    		}

    		double start2d = valueAxis.valueToJava2D(start, dataArea,
    				plot.getRangeAxisEdge());
    		double end2d = valueAxis.valueToJava2D(end, dataArea,
    				plot.getRangeAxisEdge());
    		double low = Math.min(start2d, end2d);
    		double high = Math.max(start2d, end2d);

    		PlotOrientation orientation = plot.getOrientation();
    		Rectangle2D rect = null;
    		if (orientation == PlotOrientation.VERTICAL) {
    			// clip top and bottom bounds to data area
    			low = Math.max(low, dataArea.getMinY());
    			high = Math.min(high, dataArea.getMaxY());
    			rect = new Rectangle2D.Double(dataArea.getMinX(),
    					low, dataArea.getWidth(),
    					high - low);
    		}
    		else if (orientation == PlotOrientation.HORIZONTAL) {
    			// clip left and right bounds to data area
    			low = Math.max(low, dataArea.getMinX());
    			high = Math.min(high, dataArea.getMaxX());
    			rect = new Rectangle2D.Double(low,
    					dataArea.getMinY(), high - low,
    					dataArea.getHeight());
    		}

    		final Composite originalComposite = g2.getComposite();
    		g2.setComposite(AlphaComposite.getInstance(
    				AlphaComposite.SRC_OVER, marker.getAlpha()));
    		Paint p = marker.getPaint();
    		if (p instanceof GradientPaint) {
    			GradientPaint gp = (GradientPaint) p;
    			GradientPaintTransformer t = im.getGradientPaintTransformer();
    			if (t != null) {
    				gp = t.transform(gp, rect);
    			}
    			g2.setPaint(gp);
    		}
    		else {
    			g2.setPaint(p);
    		}
    		g2.fill(rect);

    		// now draw the outlines, if visible...
    		if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
    			if (orientation == PlotOrientation.HORIZONTAL) {
    				Line2D line = new Line2D.Double();
    				double y0 = dataArea.getMinY();
    				double y1 = dataArea.getMaxY();
    				g2.setPaint(im.getOutlinePaint());
    				g2.setStroke(im.getOutlineStroke());
    				if (range.contains(start)) {
    					line.setLine(start2d, y0, start2d, y1);
    					g2.draw(line);
    				}
    				if (range.contains(end)) {
    					line.setLine(end2d, y0, end2d, y1);
    					g2.draw(line);
    				}
    			}
    			else if (orientation == PlotOrientation.VERTICAL) {
    				Line2D line = new Line2D.Double();
    				double x0 = dataArea.getMinX();
    				double x1 = dataArea.getMaxX();
    				g2.setPaint(im.getOutlinePaint());
    				g2.setStroke(im.getOutlineStroke());
    				if (range.contains(start)) {
    					line.setLine(x0, start2d, x1, start2d);
    					g2.draw(line);
    				}
    				if (range.contains(end)) {
    					line.setLine(x0, end2d, x1, end2d);
    					g2.draw(line);
    				}
    			}
    		}

    		String label = marker.getLabel();
    		RectangleAnchor anchor = marker.getLabelAnchor();
    		if (label != null) {
    			Font labelFont = marker.getLabelFont();
    			g2.setFont(labelFont);
    			g2.setPaint(marker.getLabelPaint());
    			RectangleInsets markerOffset = marker.getLabelOffset();
    			LengthAdjustmentType labelOffsetType = marker.getLabelOffsetType();
    			Rectangle2D anchorRect = null;
    			if (orientation == PlotOrientation.VERTICAL) {
    				anchorRect = markerOffset.createAdjustedRectangle(rect,
    						LengthAdjustmentType.CONTRACT, labelOffsetType);
    			}
    			else if (orientation == PlotOrientation.HORIZONTAL) {
    				anchorRect = markerOffset.createAdjustedRectangle(rect,
    						labelOffsetType, LengthAdjustmentType.CONTRACT);
    			}
    			Point2D coordinates = RectangleAnchor.coordinates(anchorRect, anchor);
    			TextUtilities.drawAlignedString(label, g2,
    					(float) coordinates.getX(), (float) coordinates.getY(),
    					marker.getLabelTextAnchor());
    		}
    		g2.setComposite(originalComposite);
    	}