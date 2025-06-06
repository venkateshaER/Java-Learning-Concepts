package com.bikes;

import com.bikes.exceptions.InvalidBikeModelExecption;
import com.bikes.model.Bike;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BikeCollections {
    public static void main(String[] args){
        // Generics and collections
        List<Bike<String>> bikes =new ArrayList<>();
        bikes.add(new Bike<>("Test ",-1));
        bikes.add(new Bike<>("Triumph Speed",400));
        bikes.add(new Bike<>("Yamaha FZ",150));
        bikes.add(new Bike<>("Honda CB",350));
        bikes.add(new Bike<>("RE Guerrilla",450));
        bikes.add(new Bike<>("RE Himalayan",450));

        //Exception handling
        for(Bike<String> bike: bikes){
            try {
                validateBike(bike);
                System.out.println("Valid bike: "+bike.getModel());
            } catch (InvalidBikeModelExecption e) {
                System.err.println("Error: "+ e.getMessage());
            }catch (Exception e){
                System.err.println("General Error: "+ e.getMessage());
            }
            finally {
                System.out.println("Finally block executed");
            }
        }

        // JAVA 8 Features
        List<String> highEndBikes=
                bikes.stream()
                        .filter(b->b.getCc()>250)
                        .map(Bike::getModel)
                        .collect(Collectors.toList());
        System.out.println("High CC bikes:" +highEndBikes);

        // Optional
        Optional<String> optionalModel = getBikeModelById(3);
        System.out.println("Bike model: "+ optionalModel.orElse("Unknown"));

        // Date/time
        LocalDate registrationDate = LocalDate.now();
        LocalDate insurance = registrationDate.plusYears(1);
        System.out.println("Registration Date: "+registrationDate+", Insurance Expiry: "+ insurance);

        // garbage collection
        String temp = new String("Temp bike");
        System.out.println("temp object "+temp);
        temp=null;
        System.out.println("temp object is assigned to null");
        System.gc();

    }
    public static void validateBike(Bike<String> bike) throws InvalidBikeModelExecption {
        try{
            if(bike.getCc()<0){
                throw new IllegalArgumentException("CC can't be negative");
            }
            else if (bike.getCc() < 250) {
                throw new InvalidBikeModelExecption(bike.getModel()+" is less CC bike");
            }
        }catch (IllegalArgumentException e){
            throw new InvalidBikeModelExecption("Invalid input for CC ", e);
        }

    }

    public static Optional<String> getBikeModelById(int id){

        Map<Integer, String> bikeMap = new HashMap<>();
        bikeMap.put(1,"Triumph Speed");
        bikeMap.put(2,"Yamaha FZ");
        bikeMap.put(3,"Honda CB");
        bikeMap.put(4,"RE Himalayan");

        return Optional.ofNullable(bikeMap.get(id));
    }
}
