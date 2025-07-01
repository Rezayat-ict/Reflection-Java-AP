import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProfessorsLaboratory {
    public static Animal createInstance(String className, String name, String DNA){
        //TODO
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(String.class, String.class);
            return (Animal) constructor.newInstance(name, DNA);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public static String getLowestCommonAncestor(Animal animal1, Animal animal2){
        //TODO
        ArrayList<String> ancestors1 = new ArrayList<>();
        Class<?> clazz1 = animal1.getClass();
        Class<?> clazz2 = animal2.getClass();
        while (clazz1!=null){
            ancestors1.add(clazz1.getSimpleName());
            clazz1 = clazz1.getSuperclass();
        }
        while (clazz2!=null){
            if (ancestors1.contains(clazz2.getSimpleName())){
                return clazz2.getSimpleName();
            }
            clazz2 = clazz2.getSuperclass();
        }
        return null;
    }

    public static String getAnimalInfo(Animal animal){
        //TODO
        StringBuilder info = new StringBuilder();
        Class<?> clazz = animal.getClass();
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            Collections.addAll(fields, currentClass.getDeclaredFields());
            info.append("class ").append(currentClass.getSimpleName()).append("=>\n");
            try {
                if (!fields.isEmpty()) {
                    for (Field field : fields) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            info.append("\t").append("for all ").append(currentClass.getSimpleName()).append("-> ")
                                    .append(field.getName()).append(": ").append(field.get(animal)).append("\n");
                        }else if(Modifier.isPrivate(field.getModifiers())){
                            field.setAccessible(true);
                            info.append("\t").append("for this ").append(currentClass.getSimpleName()).append("-> ")
                                    .append(field.getName()).append(": ").append(field.get(animal)).append("\n");
                        }else if (Modifier.isPublic(field.getModifiers())){
                            field.setAccessible(true);
                            info.append("\t").append("for this ").append(currentClass.getSimpleName()).append("-> ")
                                    .append(field.getName()).append(": ").append(field.get(animal)).append("\n");
                        }
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
            currentClass = currentClass.getSuperclass();
            fields = new ArrayList<>();
        }
        return info.toString();
    }

    public static void applyGeneticModification(Animal animal1, Animal animal2){
        Animal strongest = animal1;
        Animal weaker = animal2;
        ArrayList <Object> field1 = getFields(animal1);
        ArrayList <Object> field2 = getFields(animal2);
        String DNA1 = (String) field1.get(0);
        String DNA2 = (String) field2.get(0);
        boolean warm1 = (boolean) field1.get(3);
        boolean warm2 = (boolean) field2.get(3);

        Habitat newHabitat = null;
        String newBodyColor;
        double newAverageWeight;
        int newAverageLife;

        if (warm1==warm2){
            String [] Dna = new String[2];
            Dna[0] = DNA1;
            Dna[1] = DNA2;
            Arrays.sort(Dna);
            if(Dna[0].equals(DNA1)){
                strongest = animal2;
                weaker = animal1;
                field1 = getFields(strongest);
                field2 = getFields(weaker);
            }

            Habitat habitat1 = (Habitat) field1.get(1);
            Habitat habitat2 = (Habitat) field2.get(1);
            String bodyColor1 = (String) field1.get(2);
            String bodyColor2 = (String) field2.get(2);
            double averageWeight1 = (double) field1.get(4);
            double averageWeight2 = (double) field2.get(4);
            int averageLife1 = (Integer) field1.get(5);
            int averageLife2 = (Integer) field2.get(5);
            newHabitat = habitat2;
            if (habitat1.equals(Habitat.WATER_AND_LAND) ||
                    (habitat1.equals(Habitat.LAND)&&habitat2.equals(Habitat.WATER))||
                    (habitat1.equals(Habitat.WATER)&&habitat2.equals(Habitat.LAND))){
                newHabitat = Habitat.WATER_AND_LAND;
            }
            newBodyColor = bodyColor1+"/"+bodyColor2;
            newAverageWeight = (averageWeight1+averageWeight2)/2;
            newAverageLife = (averageLife1+averageLife2)/2;
            setFields(newHabitat,newBodyColor,newAverageWeight,newAverageLife,(String) field1.get(0),weaker);

            Class<?> clazz1 = strongest.getClass();
            Class<?> clazz2 = weaker.getClass();
            ArrayList<String> humanUsesST;
            ArrayList<String> humanUsesWE;
            try {
                Field humanUses1 = clazz1.getField("humanUses");
                Field humanUses2 = clazz2.getField("humanUses");
                if (humanUses1 == null || humanUses2 == null) {
                    return;
                }
                humanUsesST = (ArrayList<String>) humanUses1.get(strongest);
                humanUsesWE = (ArrayList<String>) humanUses2.get(weaker);
                if (humanUsesST != null && humanUsesWE != null) {
                    for (String use : humanUsesST) {
                        if (!humanUsesWE.contains(use)) {
                            humanUsesWE.add(use);
                        }
                    }
                }
                humanUses2.set(weaker,humanUsesWE);
            } catch (Exception e) {
                return;
            }
        }
        //TODO
    }

    public static double averageReproductionsPerYear(Animal animal){
        //TODO
        Class<?> clazz = animal.getClass();
        Method survivalMethod = null;
        Method reproductionMethod = null;
        while (clazz != null) {
            try {
                survivalMethod = clazz.getDeclaredMethod("survivalToMaturityRate");
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        clazz = animal.getClass();
        while (clazz != null) {
            try {
                reproductionMethod = clazz.getDeclaredMethod("yearlyReproductionYield");
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        try {
            double num1 = (double) survivalMethod.invoke(animal);
            int num2 = (int) reproductionMethod.invoke(animal);
            return num2*num1;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    static ArrayList<Object> getFields (Animal animal){
        Class<?> clazz = animal.getClass();
        ArrayList<Object> fields = new ArrayList<>();
        boolean warm1;
        String DNA;
        Habitat habitat;
        String bodyColor;
        double averageWeight;
        int averageLifespan;

        while (clazz != null) {
            if(clazz.getSimpleName().equalsIgnoreCase("animal")){
                try {
                    Field DNAField = clazz.getDeclaredField("DNA");
                    Field habitatField = clazz.getDeclaredField("habitat");
                    Field bodyColorField = clazz.getDeclaredField("bodyColor");
                    Field warm1Field = clazz.getDeclaredField("isWarmBlooded");
                    Field averageWeightField = clazz.getDeclaredField("averageWeight");
                    Field averageLifespanField = clazz.getDeclaredField("averageLifespan");

                    DNAField.setAccessible(true);
                    habitatField.setAccessible(true);
                    bodyColorField.setAccessible(true);
                    warm1Field.setAccessible(true);
                    averageWeightField.setAccessible(true);
                    averageLifespanField.setAccessible(true);
                    DNA = (String) DNAField.get(animal);
                    habitat = (Habitat) habitatField.get(animal);
                    bodyColor = (String) bodyColorField.get(animal);
                    warm1 = (boolean) warm1Field.get(animal);
                    averageWeight = (double) averageWeightField.get(animal);
                    averageLifespan = (int)averageLifespanField.get(animal);

                    fields.add(DNA);
                    fields.add(habitat);
                    fields.add(bodyColor);
                    fields.add(warm1);
                    fields.add(averageWeight);
                    fields.add(averageLifespan);

                    return fields;
                }catch (Exception e){
                    throw new RuntimeException();
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    static void setFields (Habitat newHabitat, String newBodyColor, double newAverageWeight,
                           int newAverageLife, String dna , Animal animal){
        Class<?> clazz = animal.getClass();
        while (clazz != null) {
            if(clazz.getSimpleName().equalsIgnoreCase("animal")){
                try {
                    Field habitatField = clazz.getDeclaredField("habitat");
                    Field name = clazz.getDeclaredField("name");
                    Field DNA = clazz.getDeclaredField("DNA");
                    Field bodyColorField = clazz.getDeclaredField("bodyColor");
                    Field averageWeightField = clazz.getDeclaredField("averageWeight");
                    Field averageLifespanField = clazz.getDeclaredField("averageLifespan");

                    DNA.setAccessible(true);
                    name.setAccessible(true);
                    habitatField.setAccessible(true);
                    bodyColorField.setAccessible(true);
                    averageWeightField.setAccessible(true);
                    averageLifespanField.setAccessible(true);

                    name.set(animal,name.get(animal)+"Enhanced");
                    DNA.set(animal,DNA.get(animal)+dna);
                    habitatField.set(animal, newHabitat);
                    bodyColorField.set(animal, newBodyColor);
                    averageWeightField.set(animal, newAverageWeight);
                    averageLifespanField.set(animal, newAverageLife);

                }catch (Exception e){
                    throw new RuntimeException();
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
