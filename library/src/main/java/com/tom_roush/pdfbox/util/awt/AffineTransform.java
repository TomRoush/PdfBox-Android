package org.apache.pdfbox.util.awt;

import android.graphics.PointF;

/**
 * Represents a affine transformation between 2 points
 * @see org.apache.pdfbox.util.Matrix
 * 
 * [ x']   [ m00 m01 m02 ] [ x ]   [ m00*x + m01*y + m02 ]
 * [ y'] = [ m10 m11 m12 ] [ y ] = [ m10*x + m11*y + m12 ]
 * [ 1 ]   [  0   0   1  ] [ 1 ]   [          1          ]
 * 
 * Based on the AWT implementation
 * 
 * @author bomski
 * @author Tom Roush
 */
public class AffineTransform
{
	private double m00, m10, m01, m11, m02, m12;

	/**
	 * Creates an identity AffineTransform
	 * 
	 * [ 1 0 0 ]
	 * [ 0 1 0 ]
	 * [ 0 0 1 ]
	 */
	public AffineTransform()
	{
		m00 = m11 = 1;
	}

	/**
	 * Creates a new AffineTransfrom that copies the given AffineTransform
	 * 
	 * @param tx the AffineTransform to copy from
	 */
	public AffineTransform(AffineTransform tx)
	{
		setTransform(tx);
	}

	/**
	 * Creates a new AffineTransform with the given values
	 * 
	 * [ m00 m01 m02 ]
	 * [ m10 m11 m12 ]
	 * [  0   0   1  ]
	 * 
	 * @param m00 the x scaling component
	 * @param m10 the y shearing component
	 * @param m01 the x shearing component
	 * @param m11 the y scaling component
	 * @param m02 the x translation component
	 * @param m12 the y translation component
	 */
	public AffineTransform(double m00, double m10, double m01, double m11, double m02, double m12)
	{
		this.m00 = m00;
		this.m10 = m10;
		this.m01 = m01;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;
	}
	
	/**
	 * Creates a new AffineTransform with the given values
	 * 
	 * @param d an array of doubles that holds the values
	 */
	public AffineTransform(double[] d)
	{
		m00 = d[0];
		m10 = d[1];
		m01 = d[2];
		m11 = d[3];
		if (d.length >= 6)
		{
			m02 = d[4];
			m12 = d[5];
		}
	}
	
	/**
	 * Creates an AffineTransform from an android.graphics.Matrix
	 * 
	 * @param matrix the matrix to copy from
	 */
	public AffineTransform(android.graphics.Matrix matrix)
	{
		float[] values = new float[9];
		matrix.getValues(values);
		m00 = values[0];
		m01 = values[1];
		m02 = values[2];
		m10 = values[3];
		m11 = values[4];
		m12 = values[5];
	}
	
	/**
	 * Returns a translation transform:
	 * 
	 * [ 1 0 tx ]
	 * [ 0 1 ty ]
	 * [ 0 0 1  ]
	 *
	 * @param tx the x translation distance
	 * @param ty the y translation distance
	 * @return the translating transform
	 */
	public static AffineTransform getTranslateInstance(double tx, double ty)
	{
		AffineTransform t = new AffineTransform();
		t.m02 = tx;
		t.m12 = ty;
		return t;
	}
	
	/**
	 * Returns a scaling transform:
	 * 
	 * [ sx 0  0 ]
	 * [ 0  sy 0 ]
	 * [ 0  0  1 ]
	 *
	 * @param sx the x scaling factor
	 * @param sy the y scaling factor
	 * @return the scaling transform
	 */
	public static AffineTransform getScaleInstance(double sx, double sy)
	{
		AffineTransform t = new AffineTransform();
		t.setToScale(sx, sy);
		return t;
	}

	/**
	 * Copies the values of the given AffineTransform to this one
	 * 
	 * @param tx the AffineTransform to copy from
	 */
	public void setTransform(AffineTransform tx)
	{
		m00 = tx.m00;
		m01 = tx.m01;
		m02 = tx.m02;
		m10 = tx.m10;
		m11 = tx.m11;
		m12 = tx.m12;
	}

	/**
	 * Returns the X coordinate scaling factor of the matrix.
	 * 
	 * @return m00
	 */
	public double getScaleX()
	{
		return m00;
	}

	/**
	 * Returns the Y coordinate shearing factor of the matrix.
	 * 
	 * @return m10
	 */
	public double getShearY()
	{
		return m10;
	}

	/**
	 * Returns the X coordinate shearing factor of the matrix.
	 * 
	 * @return m01
	 */
	public double getShearX()
	{
		return m01;
	}

	/**
	 * Returns the Y coordinate scaling factor of the matrix.
	 * 
	 * @return m11
	 */
	public double getScaleY()
	{
		return m11;
	}

	/**
	 * Returns the X coordinate translation factor of the matrix.
	 * 
	 * @return m02
	 */
	public double getTranslateX()
	{
		return m02;
	}

	/**
	 * Returns the Y coordinate translation factor of the matrix.
	 * 
	 * @return m12
	 */
	public double getTranslateY()
	{
		return m12;
	}

	/**
	 * Returns the matrix of values used in this AffineTransform
	 * 
	 * @param values the array of values
	 */
	public void getMatrix(double[] values)
	{
		values[0] = m00;
		values[1] = m10;
		values[2] = m01;
		values[3] = m11;
		values[4] = m02;
		values[5] = m12;
	}
	
	/**
	 * Perform this transformation on the given source point, and store the
	 * result in the destination (creating it if necessary). It is safe for
	 * src and dst to be the same.
	 *
	 * @param src the source point
	 * @param dst the destination, or null
	 * @return the transformation of src, in dst if it was non-null
	 * @throws NullPointerException if src is null
	 */
	public PointF transform(PointF src, PointF dst)
	{
		if (dst == null)
			dst = new PointF();
		double x = src.x;
		double y = src.y;
		double nx = m00 * x + m01 * y + m02;
		double ny = m10 * x + m11 * y + m12;
		dst.set((float) nx, (float) ny);
		return dst;
	}

	/**
	 * Performs a transformation on an array of points
	 * 
	 * @param srcPts the array of source points
	 * @param srcOff the starting offset of the source array
	 * @param dstPts the array of destination points
	 * @param dstOff the starting offset of the destination array
	 * @param num the number of points to transform
	 */
	public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int num)
	{
		if (srcPts == dstPts && dstOff > srcOff && num > 1 && srcOff + 2 * num > dstOff)
		{
			double[] d = new double[2 * num];
			System.arraycopy(srcPts, srcOff, d, 0, 2 * num);
			srcPts = d;
		}
		
		while (--num >= 0)
		{
			double x = srcPts[srcOff++];
			double y = srcPts[srcOff++];
			dstPts[dstOff++] = m00 * x + m01 * y + m02;
			dstPts[dstOff++] = m10 * x + m11 * y + m12;
		}
	}

	/**
	 * Performs a transformation on an array of points
	 * 
	 * @param srcPts the array of source points
	 * @param srcOff the starting offset of the source array
	 * @param dstPts the array of destination points
	 * @param dstOff the starting offset of the destination array
	 * @param num the number of points to transform
	 */
	public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int num)
	{
		if (srcPts == dstPts && dstOff > srcOff && num > 1 && srcOff + 2 * num > dstOff)
		{
			float[] f = new float[2 * num];
			System.arraycopy(srcPts, srcOff, f, 0, 2 * num);
			srcPts = f;
		}
		
		while (--num >= 0)
		{
			float x = srcPts[srcOff++];
			float y = srcPts[srcOff++];
			dstPts[dstOff++] = (float) (m00 * x + m01 * y + m02);
			dstPts[dstOff++] = (float) (m10 * x + m11 * y + m12);
		}
	}

	/**
	 * Concatenates a scale unto this matrix
	 * 
	 * @param sx the x scaling factor
	 * @param sy the y scaling factor
	 */
	public void scale(double sx, double sy)
	{
		m00 *= sx;
		m01 *= sy;
		m10 *= sx;
		m11 *= sy;
	}

	/**
	 * Concatenates a translation to this matrix
	 * 
	 * @param tx the x translation distance
	 * @param ty the y translation distance
	 */
	public void translate(double tx, double ty)
	{
		m02 += tx * m00 + ty * m01;
		m12 += tx * m10 + ty * m11;
	}

	public float[] transform(float[] src, float[] dst)
	{
		if (dst == null)
		{
			dst = new float[2];
		}
		float x = src[0];
		float y = src[1];
		float nx = (float) (m00 * x + m01 * y + m02);
		float ny = (float) (m10 * x + m11 * y + m12);
		dst[0] = nx;
		dst[1] = ny;
		return dst;
	}

	/**
	 * Concatenates a rotation to this matrix
	 * 
	 * @param theta the angle to rotate by
	 */
	public void rotate(double theta)
	{
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		double n00 = m00 * c + m01 * s;
		double n01 = m00 * -s + m01 * c;
		double n10 = m10 * c + m11 * s;
		double n11 = m10 * -s + m11 * c;
		m00 = n00;
		m01 = n01;
		m10 = n10;
		m11 = n11;
	}
	
	/**
	 * Set this transform to a scale:
	 * 
	 * [ sx 0  0 ]
	 * [ 0  sy 0 ]
	 * [ 0  0  1 ]
	 *
	 * @param sx the x scaling factor
	 * @param sy the y scaling factor
	 */
	public void setToScale(double sx, double sy)
	{
		m00 = sx;
		m01 = m02 = m10 = m12 = 0;
		m11 = sy;
	}
	
	/**
	 * Set this transform to the result of performing the original version of
	 * this followed by tx. This is commonly used when chaining transformations
	 * from one space to another. In matrix form:
	 * <pre>
	 * [ this ] = [ this ] x [ tx ]
	 * </pre>
	 *
	 * @param tx the transform to concatenate
	 * @throws NullPointerException if tx is null
	 * @see #preConcatenate(AffineTransform)
	 */
	public void concatenate(AffineTransform tx)
	{
		double n00 = m00 * tx.m00 + m01 * tx.m10;
		double n01 = m00 * tx.m01 + m01 * tx.m11;
		double n02 = m00 * tx.m02 + m01 * tx.m12 + m02;
		double n10 = m10 * tx.m00 + m11 * tx.m10;
		double n11 = m10 * tx.m01 + m11 * tx.m11;
		double n12 = m10 * tx.m02 + m11 * tx.m12 + m12;
		m00 = n00;
		m01 = n01;
		m02 = n02;
		m10 = n10;
		m11 = n11;
		m12 = n12;
	}

	/**
	 * Tests if this matrix is an identity matrix
	 * 
	 * @return whether this is the identity matrix
	 */
	public boolean isIdentity()
	{
		return (m00 == 1 && m01 == 0 && m02 == 0 && m10 == 0 && m11 == 1 && m12 == 0);
	}
	
	/**
	 * Returns this AffineTransform as an android.graphics.Matrix
	 * 
	 * @return the matrix
	 */
	public android.graphics.Matrix toMatrix()
	{
		android.graphics.Matrix retval = new android.graphics.Matrix();
		retval.setValues(new float[]{
				(float) m00, (float) m01, (float) m02,
				(float) m10, (float) m11, (float) m12,
				0f, 0f, 1f
				});
		return retval;
	}
}
