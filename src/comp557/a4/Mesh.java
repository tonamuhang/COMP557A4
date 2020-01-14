package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.Map;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 7: ray triangle intersection for meshes
		for(int[] face : soup.faceList) {
                Vector3d n = new Vector3d();
                Point3d v0 = new Point3d(soup.vertexList.get(face[0]).p);
                Point3d v1 = new Point3d(soup.vertexList.get(face[1]).p);
                Point3d v2 = new Point3d(soup.vertexList.get(face[2]).p);


                Vector3d e0 = new Vector3d();
                Vector3d e1 = new Vector3d();
                Vector3d e2 = new Vector3d();
                e0.sub(v1, v0);
                e1.sub(v2, v1);
                e2.sub(v0, v2);

                Vector3d temp = new Vector3d(e2);
                temp.negate();
                n.cross(e0, temp);
                n.normalize();

                Point3d p = new Point3d(ray.eyePoint);
                Vector3d p_v = new Vector3d(p);
                Vector3d a = new Vector3d(v0);
                a.sub(p_v);

                double t = a.dot(n) / ray.viewDirection.dot(n);

                temp = new Vector3d(ray.viewDirection);
                temp.scale(t);
                p.add(temp);

                Vector3d aout = new Vector3d(p);
                Vector3d bout = new Vector3d(p);
                Vector3d cout = new Vector3d(p);

                aout.sub(v0);
                bout.sub(v1);
                cout.sub(v2);

                aout.cross(e0, aout);
                bout.cross(e1, bout);
                cout.cross(e2, cout);

                if(n.dot(aout) > 0 && n.dot(bout) > 0 && n.dot(cout) > 0){
                    if(t < result.t && t > 1e-9) {
                        result.t = t;
                        result.n.set(n);
                        result.p.set(p);
                        result.material = this.material;
                    }
                }

        }
	}

}
