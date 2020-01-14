package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Vector;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );

	public float time0 = 0;
	public Point3d[] points = new Point3d[5];


    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result) {
    
        // TODO: Objective 2: intersection of ray with sphere
//        Vector3d d = ray.viewDirection;
//        Vector3d td = new Vector3d();
//        Point3d e = new Point3d(ray.eyePoint);
//        Vector3d p = new Vector3d(e.x - this.center.x, e.y - this.center.y, e.z - this.center.z);
//
//        double dp = d.dot(p);
//        double dd = d.dot(d);
//        double pp = p.dot(p);
//
//        double t = (-dp - Math.sqrt(Math.pow(dp, 2) - dd * (pp - 1))) / dd;
//
//        if(t > 0 && t < result.t){
//            result.t = t;
//            td.set(d);
//            td.scale(t);
//
//            result.material = this.material;
//            result.p.set(new Point3d(p.x + td.x, p.y + td.y, p.z + td.z));
//            result.n.set(p);
//        }


        Vector3d d = new Vector3d(ray.viewDirection);
        Point3d p = new Point3d(ray.eyePoint);
        Vector3d p_v = new Vector3d(p);

        double dp = d.dot(p_v);
        double dd = d.dot(d);
        double pp = p_v.dot(p_v);

        double t = (-dp - Math.sqrt(Math.pow(dp, 2) - dd * (pp - this.radius * this.radius))) / dd;


        Vector3d o = new Vector3d(p);
        o.sub(this.center);

        double a = d.dot(ray.viewDirection);
        double b = 2 * d.dot(o);
        double c = o.dot(o) - this.radius * this.radius;

        double delta = b*b - 4 * a * c;

        t = (-b - Math.sqrt(delta))/ (2.0 * a);
        if(t < 0){
            t = (-b + Math.sqrt(delta))/ (2.0 * a);
        }



        if(t < result.t && t > 1e-9){
            result.t = t;
            result.material = this.material;

            Point3d intersect = new Point3d();
            ray.getPoint(t, intersect);
            result.p.set(intersect);

            Vector3d normal = new Vector3d(2*(result.p.x - this.center.x), 2*(result.p.y - this.center.y), 2*(result.p.z - this.center.z));
            normal.normalize();
            result.n.set(normal);
        }

    }




    
}
