/*
 * Copyright (c) 2016.
 * This code is the property of ARMINES/MINES Saint-Etienne
 */

package demand;

import org.json.simple.JSONObject;

public abstract class Resource {
    public static int ID_RESOURCE = 1;
    private String name;
    private int idResource;
    
    public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	
    
    
    public Resource() {
        setIdResource(ID_RESOURCE++);
    }

    public static void init() {
        ID_RESOURCE = 1;
    }

    public int getIdResource() {
        return idResource;
    }

    protected void setIdResource(int idResource) {
    	
        this.idResource = idResource;
    }
    
    
    /**
     * 
     * @param rgd the generator of the instance value
     * @return a JSONObject of the object
     */
	public abstract JSONObject generate(RequestGeneratorDemand rgd) ;
	
	public static Resource factory(JSONObject jo) {
		String resourceType = (String) jo.get("resourceType");
		switch(resourceType) {
		case "Trip": 
			return Trip.factory(jo);
		}
		return null;
	}

	protected void completeName(String additional) {
		name+= "_"+additional;
	}
}
