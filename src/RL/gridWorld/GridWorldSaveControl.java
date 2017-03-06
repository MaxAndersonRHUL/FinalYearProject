package RL.gridWorld;

import java.io.*;

/**
 * Created by max on 24/02/2017.
 */
public class GridWorldSaveControl {

    public static String currentEnvironmentSave;

    public static boolean loadEnvironmentFromFile(String nFileName) {
        String directoryName = "GridWorldSaves/";
        String fileName = nFileName;

        File directory = new File(String.valueOf(directoryName));
        if(!directory.exists()) {
            return false;
        }

        File file = new File(directoryName + "/" + fileName);

        GridWorldModel nModel = null;

        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            nModel = (GridWorldModel) in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i) {
            System.out.println(i);
            return false;
        }catch(ClassNotFoundException c) {
            c.printStackTrace();
            return false;
        }

        currentEnvironmentSave = nFileName;
        loadExistingModelWithNewModel(nModel);
        return true;
    }

    public static void loadExistingModelWithNewModel(GridWorldModel newModel) {
        if(newModel == null) {
            return;
        }
        GridWorldModel existingModel = GridWorldModel.getInstance();
        existingModel.states = newModel.states;
        existingModel.mainAgent = newModel.mainAgent;
        existingModel.gridSizeX = newModel.gridSizeX;
        existingModel.gridSizeY = newModel.gridSizeY;
        existingModel.startingState = newModel.startingState;
    }

    public static boolean saveCurrentEnvironment(String name) {
        try {
            String directoryName = "GridWorldSaves/";
            String fileName = name;

            File directory = new File(directoryName);
            if(!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directoryName + "/" + fileName);

            FileOutputStream fileOut =
                    new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(GridWorldModel.getInstance());
            out.close();
            fileOut.close();


        } catch (IOException o) { System.out.println(o);
         return false;
        }
        return true;
    }

}
