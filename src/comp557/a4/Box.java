package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner.
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;

    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
        Point3d e = new Point3d(ray.eyePoint);
        Vector3d d = new Vector3d(ray.viewDirection);

        double txMax = Math.max((min.x - e.x) / d.x, (max.x - e.x) / d.x);
        double tyMax = Math.max((min.y - e.y) / d.y, (max.y - e.y) / d.y);
        double tzMax = Math.max((min.z - e.z) / d.z, (max.z - e.z) / d.z);
        double txMin = Math.min((min.x - e.x) / d.x, (max.x - e.x) / d.x);
        double tyMin = Math.min((min.y - e.y) / d.y, (max.y - e.y) / d.y);
        double tzMin = Math.min((min.z - e.z) / d.z, (max.z - e.z) / d.z);

        double tMin = Math.max(txMin,Math.max(tyMin, tzMin));
        double tMax = Math.min(txMax,Math.min(tyMax, tzMax));

        if(tMin < tMax && tMin>0) {
            Vector3d r = new Vector3d(d);
            r.scale(tMin);
            r.add(e);
            result.p = new Point3d(r);
            result.t = tMin;
            result.material = material;

            if(Math.abs(result.p.x-min.x)<1e-9)
                result.n.set(-1,0,0);
            else if(Math.abs(result.p.x-max.x)<1e-9)
                result.n.set(1,0,0);
            else if(Math.abs(result.p.y-min.y)<1e-9)
                result.n.set(0,-1,0);
            else if(Math.abs(result.p.y-max.y)<1e-9)
                result.n.set(0,1,0);
            else if(Math.abs(result.p.z-min.z)<1e-9)
                result.n.set(0,0,-1);
            else if(Math.abs(result.p.z-max.z)<1e-9)
                result.n.set(0,0,1);
        }


	}

}
