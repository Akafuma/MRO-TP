
import java.util.ArrayList;

import org.xcsp.common.IVar.Var;
import org.xcsp.modeler.api.ProblemAPI;
import org.xcsp.modeler.implementation.NotData;


public class Radio implements ProblemAPI
{
    int[] regions;

    Station[] stations;
    class Station
    {
        int num;
        int region;
        int delta;
        int[] emetteur;
        int[] recepteur;
    }

    Interference[] interferences;
    class Interference
    {
        int x;
        int y;
        int Delta;
    }

    Liaison[] liaisons;
    class Liaison
    {
        int x;
        int y;
    }

    @NotData
    int n_stations;
    @NotData
    int n_interfer;
    @NotData
    int n_liaisons;

    @Override
    public void model()
    {
        n_stations = stations.length;
        n_interfer = interferences.length;
        n_liaisons = liaisons.length;

        // Variables obligatoire au problème et domaine
        Var[] eme = array("eme", size(stations.length), i -> dom(stations[i].emetteur),
                "eme[i]: frequence d'emission de la station i");
        Var[] rec = array("rec", size(stations.length), i -> dom(stations[i].recepteur),
                "rec[i]: frequence de reception de la station i");


        // Contraintes du problème de base
        // 1_ ecart de frequence entre eme et rec d'une station.
        forall(range(n_stations), i -> equal(dist(eme[i], rec[i]), stations[i].delta));
        
        // 2_ ecart de frequence necessaire entre stations proches
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
        
        // 3_ liaisons entre stations
        for(int i = 0; i < n_liaisons; i++)
        {
            equal(eme[liaisons[i].x], rec[liaisons[i].y]);  // eme[i] == rec[j]
            equal(rec[liaisons[i].x], eme[liaisons[i].y]);  // rec[i] == eme[j]
        }
        
        // 4_ nombre de frequences par regions
        ArrayList< ArrayList<Var>> frequenciesByRegions = new ArrayList<ArrayList<Var>>();      
        for(int i = 0; i < regions.length; i++)
        	frequenciesByRegions.add(new ArrayList<Var>());
        
        for(int i = 0; i < n_stations; i++)
        {
        	int r = stations[i].region;
        	frequenciesByRegions.get(r).add(eme[i]);
        	frequenciesByRegions.get(r).add(rec[i]);
        }
        
        for(int i = 0; i < regions.length; i++)
        {
        	Var[] frequencies_region_i = (Var[]) frequenciesByRegions.get(i).toArray();
        	nValues(frequencies_region_i, LE, regions[i]);
        }

        //minimiser le nombre de frequences utilisees
        if(modelVariant("m1"))
        {
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
        	nValues(allVar, EQ, min);
        	minimize(min);
        }
        //utiliser les frequences les plus basses possibles,
        if(modelVariant("m2"))
        {

        }
        //minimiser la largeur de la bande de frequences utilisées
        if(modelVariant("m2"))
        {

        }
    }
}
