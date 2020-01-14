package comp557.a4;

import java.util.*;
import javax.vecmath.*;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    public boolean blur = false;
    public int blur_sample;
    public FastPoissonDisk fpd = new FastPoissonDisk();
    public double focallength;


    public boolean motion = false;
    public double speed;

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);

//        Ray ray = new Ray();
//        Ray blur_ray = new Ray();

        Random rand = new Random();

        double[] offset = new double[2];
        SceneNode root = null;   //TODO: implement sceneNode
        if(surfaceList.get(0) instanceof SceneNode){
            root = (SceneNode) surfaceList.get(0);
        }


        for ( int j = 0; j < h && !render.isDone(); j++ ) {
            for ( int i = 0; i < w && !render.isDone(); i++ ) {
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)
                ArrayList<Ray> rays = new ArrayList<>();

                if(render.jitter){
                    offset[0] = (rand.nextDouble() - 0.5) * 0.1;
                    offset[1] = (rand.nextDouble() - 0.5) * 0.1;
                }

                if(blur){
                    Ray blur_ray = new Ray();
                    generateRay(i, j, offset, cam, blur_ray);
                    Point3d fp = new Point3d(blur_ray.eyePoint);
                    Vector3d temp = new Vector3d(blur_ray.viewDirection);
                    temp.scale(focallength);
                    fp.add(temp);

                    for(int k = 0; k < blur_sample; k++){
                        Point2d p = new Point2d();
                        Ray focal = new Ray();

                        fpd.get(p, k, blur_sample);

                        Camera cam_temp = new Camera();
                        cam_temp.to.set(cam.to);
                        cam_temp.from = new Point3d(cam.from.x + p.x/10f, cam.from.y + p.y/10f,
                                cam.from.z);
                        cam_temp.up.set(cam.up);
                        cam_temp.fovy = cam.fovy;
                        cam_temp.imageSize.setSize(cam.imageSize);


                        focal.eyePoint.set(cam_temp.from);
                        focal.viewDirection.set(fp);
                        focal.viewDirection.sub(cam_temp.from);
                        focal.viewDirection.normalize();

                        rays.add(focal);
                    }

                }
                else if(motion){
                    Ray motionRay = new Ray();
                    generateRay(i, j, offset, cam, motionRay);
                    Point3d fp = new Point3d(motionRay.eyePoint);
                    Vector3d temp = new Vector3d(motionRay.viewDirection);
                    temp.scale(focallength);
                    fp.add(temp);


                }
                else {
                    Ray ray = new Ray();
                    generateRay(i, j, offset, cam, ray);
                    rays.add(ray);
                }

                // TODO: Objective 2: test for intersection with scene surfaces
                Color3f c = new Color3f(render.bgcolor);
                for(Ray r : rays) {
                    IntersectResult result = new IntersectResult();
//                IntersectResult shadowResult = new IntersectResult();


//                if(root != null){
//                    root.intersect(ray, result);
//                }
//                else {
//                    for (Intersectable intersectable : this.surfaceList) {
//                        intersectable.intersect(ray, result);
//                    }
//                }


                    for (Intersectable intersectable : this.surfaceList) {
                        intersectable.intersect(r, result);
                    }

                    // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)

                    // Here is an example of how to calculate the pixel value.
                    if (result.t < Double.POSITIVE_INFINITY) {
                        Color4f lighting = getLighting(result, cam, root, r);
                        Color3f color = new Color3f(lighting.x, lighting.y, lighting.z);
//                        c.set(color);

                        if(blur) {
                            color.scale(1f/blur_sample);
                            c.x += color.x;
                            c.y += color.y;
                            c.z += color.z;
                        }
                        else if(motion){
                            color.scale(1f/3);
                            c.x += color.x;
                            c.y += color.y;
                            c.z += color.z;
                        }
                        else {
                            c.set(color);
                        }

                    }
                }

            	c.clamp(0, 1);
            	int r = (int)(255*c.x);
                int g = (int)(255*c.y);
                int b = (int)(255*c.z);
                int a = 255;
                int argb = (a<<24 | (int)r<<16 | (int)g<<8 | (int)b);

                // update the render image
                render.setPixel(i, j, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		
		// TODO: Objective 1: generate rays given the provided parmeters

        Point3d e = new Point3d(cam.from);
        Vector3d w = new Vector3d(e);
        w.sub(cam.to);
        double distance = w.length();
        w.normalize();

        Vector3d u = new Vector3d();
        u.cross(cam.up, w);
        u.normalize();

        Vector3d v = new Vector3d();
        v.cross(w, u);
        v.normalize();

        double t = distance * (Math.tan(Math.toRadians(cam.fovy/2.0)));
        double b = -t;
        double r = t*((double)cam.imageSize.width/(double)cam.imageSize.height);
        double l = -r;

        double u_ = l + (r-l) * (i + 0.5 + offset[1]) / cam.imageSize.width;
        double v_ = -(b + (t-b) * (j + 0.5 + offset[0]) / cam.imageSize.height);

        u.scale(u_);
        v.scale(v_);
        w.scale(distance);
        w.negate();

        Vector3d s = new Vector3d(e);
        s.add(u);
        s.add(v);
        s.add(w);

        Vector3d d = new Vector3d(s);
        d.sub(e);
        d.normalize();

        ray.eyePoint.set(e);
        ray.viewDirection.set(d);
	}

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, List<Intersectable> surfaceList, final SceneNode root, IntersectResult shadowResult, Ray shadowRay) {

		// TODO: Objective 5: check for shadows and use it in your lighting computation


        //intersect the shadowray with all objects
//        if(root != null) {
//            root.intersect(shadowRay, shadowResult);
//
//        }

        // The t needed to reach the light
        double t = shadowRay.viewDirection.length();
        shadowRay.viewDirection.normalize();

        // the actual t < needed t = blocked)
//        if(root != null){
//            root.intersect(shadowRay, shadowResult);
//        }
//        else {
//            for (Intersectable surface : surfaceList) {
//                surface.intersect(shadowRay, shadowResult);
//            }
//        }

        for (Intersectable surface : surfaceList) {
            surface.intersect(shadowRay, shadowResult);
        }

        if(shadowResult.t < t && shadowResult.t > 1e-9){

            return true;
        }

        return false;
	}


	private static void generateShadowRay(final IntersectResult result, final Light light, Ray shadowRay){
	    Vector3d e = new Vector3d(result.p);
	    Vector3d d = new Vector3d(light.from);
	    d.sub(e);

	    // avoid shadow rounding errors
	    shadowRay.viewDirection.set(d);
        d.normalize();

	    d.scale(1e-9);
	    e.add(d);
	    shadowRay.eyePoint.set(e);
    }

	private Color4f getLighting(final IntersectResult result, final Camera cam, SceneNode root, Ray ray){

	    Vector3f ia = new Vector3f(this.ambient);
        Color4f color = new Color4f(ia.x* result.material.diffuse.x,
                ia.y * result.material.diffuse.y,  ia.z* result.material.diffuse.z, 0);


	    for(Light light : this.lights.values()) {

            Ray shadowRay = new Ray();
            IntersectResult shadowResult = new IntersectResult();

            generateShadowRay(result, light, shadowRay);

            if (inShadow(result, surfaceList, root, shadowResult, shadowRay)) {
                color.scale(1);
                continue;
            }
            Vector3d light_from = new Vector3d(light.from);
            light_from.sub(result.p);
            light_from.normalize();


            // Lambertian
            Vector3d temp = new Vector3d(light.from);
            temp.sub(result.p);
            temp.normalize();
            double nl = result.n.dot(temp);

            if (nl < 0) {
                nl = 0;
            }

            Vector4f kd = new Vector4f(result.material.diffuse);
            Color4f lambertian = new Color4f(kd.x * light.color.x, kd.y * light.color.y,
                    kd.z * light.color.z, 0);

            lambertian.scale((float) nl);
            lambertian.scale((float)light.power);


            // Blingphong
            Vector3d v = new Vector3d(ray.eyePoint);
            v.sub(result.p);
            v.normalize();
            Vector3d l = new Vector3d(light.from);
            l.sub(result.p);
            l.normalize();
            Vector3d bisector = new Vector3d(v);
            bisector.add(l);
            bisector.normalize();

            double nh = result.n.dot(bisector);
            if (nh < 0) {
                nh = 0;
            }

            nh = Math.pow(nh, result.material.shinyness);
            Vector4f ks = new Vector4f(result.material.specular);
            Color4f bling = new Color4f(ks.x * light.color.x, ks.y * light.color.y,
                    ks.z * light.color.z, 1);

            bling.scale((float) (nh * light.power));

            color.add(lambertian);
            color.add(bling);


        }


        return color;
    }



}
