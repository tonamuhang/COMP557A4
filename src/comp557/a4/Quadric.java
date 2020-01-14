package comp557.a4;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;


public class Quadric extends Intersectable {
    
	/**
	 * Radius of the sphere.
	 */
	public Matrix4d Q = new Matrix4d();
	public Matrix3d A = new Matrix3d();
	public Vector3d B = new Vector3d();
	public double C;
	
	/**
	 * The second material, e.g., for front and back?
	 */
	Material material2 = null;
	
	public Quadric() {
		super();
	}
	
	@Override
	public void intersect(Ray ray, IntersectResult result) {
        Vector3d e = new Vector3d(ray.eyePoint);
        Vector3d d = new Vector3d(ray.viewDirection);

        Vector3d temp = new Vector3d();

        A.transform(d, temp);
        double a = ray.viewDirection.dot(temp);
        double b = e.dot(temp);

        A.transform(e, temp);
        b += ray.viewDirection.dot(temp) - 2 * B.dot(d);;
        double c = e.dot(temp);
        c += C - 2 * B.dot(e);

        double delta = b * b - 4 * a * c;

        if(delta >= 0){
            double t1 = (-b + Math.sqrt(delta)) / (2 * a);
            double t2 = (-b - Math.sqrt(delta)) / (2 * a);
            double t = Math.min(Math.max(t1, 0), Math.max(t2, 0));
            if (t < result.t && t > 1e-9) {
                result.t = t;
                ray.getPoint(t, result.p);
                A.transform(result.p, result.n);
                result.n.scale(2);
                temp = new Vector3d(B);
                temp.scale(2);
                result.n.sub(temp);
                result.n.normalize();
                result.material = material;
            }
        }


	}
	
}
