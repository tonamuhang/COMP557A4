package comp557.a4;

import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import java.util.Vector;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;

	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 4: intersection of ray with plane

    	Vector3d d = new Vector3d(ray.viewDirection);
    	Point3d p = new Point3d(ray.eyePoint);
        Vector3d p_v = new Vector3d(p);
        Vector3d a = new Vector3d();
        a.sub(p_v);

        // a = 0
        double t = a.dot(Plane.n) / d.dot(Plane.n);



        if(t < result.t && t > 1e-9){
            result.t = t;

            Point3d intersect = new Point3d();
            ray.getPoint(t, intersect);

            result.p.set(intersect);
            result.n = new Vector3d(Plane.n);

            double i = result.p.x % 2;
            double j = result.p.z % 2;

            if(i < 0){
                i += 2;
            }
            if(j < 0){
                j += 2;
            }

            if(this.material2 != null){
                if(i >= 1){
                    if(j >= 1) {
                        result.material = this.material;
                    }
                    else{
                        result.material = this.material2;
                    }
                }
                else{
                    if(j >= 1) {
                        result.material = this.material2;
                    }
                    else{
                        result.material = this.material;
                    }
                }
            }
            else {
                result.material = this.material;
            }
        }




    }
    
}
