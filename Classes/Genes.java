package Classes;

import java.util.ArrayList;
import java.util.Collections;


public class Genes {
    public ArrayList<Integer> genes;
    private static final int genesLength = 32;

    public Genes(ArrayList<Integer> genes) {
        this.genes = genes;
    }

    public int getRandomGene(){
        return genes.get((int) (Math.random() * genesLength));
    }

    public static ArrayList<Integer> generateGenes(){
        ArrayList<Integer> G = new ArrayList<Integer>();
        for (int i = 0; i< genesLength; i++) {
            G.add( (int) (Math.random() * 8));
        }
        Collections.sort(G);
        return G;

    }
    public Genes generateChildrenGenes(Genes parent){
        ArrayList<Integer> childrenGenes = new ArrayList<Integer>();
        int cut1 =(int) (Math.random()*(genesLength-1));
        int cut2 =(int) (Math.random()*(genesLength-1-cut1))+cut1;
        int conf = (int)(Math.random()*6);

        switch (conf){
            case 0:{
                childrenGenes.addAll(this.genes.subList(0,cut1));
                childrenGenes.addAll(this.genes.subList(cut1,cut2));
                childrenGenes.addAll(parent.genes.subList(cut2,genesLength));
                break;
            }
            case 1:{
                childrenGenes.addAll(this.genes.subList(0,cut1));
                childrenGenes.addAll(parent.genes.subList(cut1,cut2));
                childrenGenes.addAll(this.genes.subList(cut2,genesLength));
                break;
            }
            case 2:{
                childrenGenes.addAll(parent.genes.subList(0,cut1));
                childrenGenes.addAll(this.genes.subList(cut1,cut2));
                childrenGenes.addAll(this.genes.subList(cut2,genesLength));
                break;
            }
            case 3:{
                childrenGenes.addAll(this.genes.subList(0,cut1));
                childrenGenes.addAll(parent.genes.subList(cut1,cut2));
                childrenGenes.addAll(parent.genes.subList(cut2,genesLength));
                break;
            }
            case 4:{
                childrenGenes.addAll(parent.genes.subList(0,cut1));
                childrenGenes.addAll(this.genes.subList(cut1,cut2));
                childrenGenes.addAll(parent.genes.subList(cut2,genesLength));
                break;
            }
            case 5:{
                childrenGenes.addAll(parent.genes.subList(0,cut1));
                childrenGenes.addAll(parent.genes.subList(cut1,cut2));
                childrenGenes.addAll(this.genes.subList(cut2,genesLength));
                break;
            }

        }

        Collections.sort(childrenGenes);
        childrenGenes = Genes.validateGenes(childrenGenes);

        return new Genes(childrenGenes);

    }

    public static ArrayList<Integer> validateGenes(ArrayList<Integer> genes){
        boolean[] exist = new boolean[]{false, false, false, false, false, false, false, false};
        boolean[] exist2 = new boolean[]{false, false, false, false, false, false, false, false};
        boolean valid = true;
        int n = 0;
        for(Integer i : genes){
            if(exist[i]){
                exist2[i] = true;
            }
            exist[i] = true;
        }
        for(int i =0; i<8; i++){
            if (!exist[i]) {
                valid = false;
                break;
            }
        }
        if(valid){
            return genes;
        }
        else {
            for(int i =0; i<8; i++){
                if(exist2[i]){
                    n++;
                }
            }
            for(int i =0; i<8; i++){
                if(! exist[i]){
                    int rand = (int)(Math.random()*n+1);
                    int j = 0;
                    while (rand != 0){
                        if(exist2[j]){
                            rand--;
                        }
                        j++;
                    }
                    j--;
                    for(int k = 0; k<genesLength;k++){
                        if(genes.get(k)==j){
                            genes.set(k,(int) (Math.random()*8)) ;
                            break;
                        }
                    }
                    Collections.sort(genes);
                    return validateGenes(genes);
                }
            }
        }
        return null;
    }

}
