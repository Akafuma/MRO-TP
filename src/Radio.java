package org.xcsp.modeler.problems;

import java.util.ArrayList;

import org.xcsp.common.IVar.Var;
import org.xcsp.modeler.api.ProblemAPI;

public class Radio implements ProblemAPI {
	
	int[] regions;
    Station[] stations;
    Interference[] interferences;
    Liaison[] liaisons;
    
    class Station
    {
        int num;
        int region;
        int delta;
        int[] emetteur;
        int[] recepteur;
    }
    
    class Interference
    {
        int x;
        int y;
        int Delta;
    }

    class Liaison
    {
        int x;
        int y;
    }

    @Override
    public void model()
    {
        int n_stations = stations.length;
        int n_interfer = interferences.length;
        int n_liaisons = liaisons.length;

        // Variables du problème
        Var[] eme = array("eme", size(stations.length), i -> dom(stations[i].emetteur),
                "eme[i]: frequence d'emission de la station i");
        Var[] rec = array("rec", size(stations.length), i -> dom(stations[i].recepteur),
                "rec[i]: frequence de reception de la station i");

        
        // Contraintes du problème de base
        // 1. Ecart de frequence entre eme et rec d'une station.
        forall(range(n_stations), i -> equal(dist(eme[i], rec[i]), stations[i].delta));
        
        // 2. Ecart de frequence necessaire entre stations proches
        for(int i = 0; i < n_interfer; i++)
        {
            int x = interferences[i].x;
            int y = interferences[i].y;
            int D = interferences[i].Delta;

            greaterEqual(dist(eme[x], eme[y]),D);  // | eme[x] - eme[y] |  >=  D
            greaterEqual(dist(eme[x], rec[y]),D);  // | eme[x] - rec[y] |  >=  D
            greaterEqual(dist(rec[x], eme[y]),D);  // | rec[x] - eme[y] |  >=  D
            greaterEqual(dist(rec[x], rec[y]),D);  // | rec[x] - rec[y] |  >=  D
        }
        
        // 3. Liaisons entre stations
        for(int i = 0; i < n_liaisons; i++)
        {
            equal(eme[liaisons[i].x], rec[liaisons[i].y]);  // eme[i] == rec[j]
            equal(rec[liaisons[i].x], eme[liaisons[i].y]);  // rec[i] == eme[j]
        }
        
        // 4. Nombre de frequences par regions
        
        //Initialisation du tableau pour sauvegarder les variables par région
        ArrayList< ArrayList<Var>> frequenciesByRegions = new ArrayList<ArrayList<Var>>();      
        for(int i = 0; i < regions.length; i++)
        	frequenciesByRegions.add(new ArrayList<Var>());
        
        //La station i est dans la région r : on ajoute eme[i] et rec[i] dans les fréquences de r
        for(int i = 0; i < n_stations; i++)
        {
        	int r = stations[i].region;
        	frequenciesByRegions.get(r).add(eme[i]);
        	frequenciesByRegions.get(r).add(rec[i]);
        }
        
        for(int i = 0; i < regions.length; i++)
        {
        	ArrayList<Var> arr = frequenciesByRegions.get(i);
        	Var[] frequencies_region_i = arr.toArray(new Var[arr.size()]);
        	nValues(frequencies_region_i, LE, regions[i]);
        }        
        
        //Minimiser le nombre de frequences utilisees
     	
        Var[] allVar = vars(eme, rec); //On récupère toutes les fréquences utilisées
       	int sum = 0;
       	for(int i = 0; i < regions.length; i++)
       		sum+=regions[i];
       	/*Le nombre maximum de fréquence utilisable est majoré par la somme
       	 *  du nombre de fréquence
       	 *  utilisable par chaque régions
       	 */
        		
       	Var min = var("min", dom(range(sum)), 
       		"min est le nombre de fréquence différentes utilisées");
       	//On compte le nombre de fréquence utilisé grâceà la contrainte nvalues et la variable min
        nValues(allVar, EQ, min);
        //On minimise donc sur la variable min
       	minimize(min);
    }
}
