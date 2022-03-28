package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import android.graphics.PointF;

class Vertex {

    public PointF point;
    public float[] color;

    Vertex(PointF p, float[] c)
    {
        point = p;
        color = c.clone();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (float f : color)
        {
            if (sb.length() > 0)
            {
                sb.append(' ');
            }
            sb.append(String.format("%3.2f", f));
        }
        return "Vertex{ " + point + ", colors=[" + sb + "] }";
    }
}
