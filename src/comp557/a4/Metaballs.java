package comp557.a4;

import java.util.ArrayList;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;


public class Metaballs extends Intersectable {

	ArrayList<Sphere> spheres = new ArrayList<Sphere>();

	double thresh = 0.2;

	double eps = 0.01;

	int MAX_MARCHING_STEP = 50;


	@Override
	public void intersect(Ray ray, IntersectResult result) {
		double t = 0;

		for(int j = 0; j < spheres.size(); j++) {

            for (int i = 0; i < MAX_MARCHING_STEP; i++) {
                Point3d p = new Point3d();
                ray.getPoint(t, p);
                double dist = signed_distance(p);

                if (dist < eps) {
                    spheres.get(j).intersect(ray, result);
                }

                t += dist;

                if (dist >= thresh) {
                    spheres.get(j).intersect(ray, result);
                }
            }

            spheres.get(j).intersect(ray, result);
        }

	}

	public double signed_distance(Point3d p){
	    double sum = 0;

        for (int i = 0; i < spheres.size(); i++)
        {
            Vector3d temp = new Vector3d(p);
            temp.sub(spheres.get(i).center);

            sum += (1 / temp.length()) /temp.length() ;
        }

        return this.thresh - sum;
    }
}

