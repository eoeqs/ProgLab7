package me.lab7.server.managers;

import me.lab7.common.models.*;

import java.time.LocalDate;
import java.util.HashMap;

public record CollectionManager(HashMap<Long, Worker> workerMap) {

    @Override
    public HashMap<Long, Worker> workerMap() {
        return workerMap;
    }


    public String add(Worker worker) {
        long key = worker.getId();
        workerMap.put(key, worker);
        return "The following element was inserted into the collection:\n" + worker + "\n";
    }

    public String replace(Worker worker) {
        long key = worker.getId();
        Worker oldWorker = workerMap.get(key);
        workerMap.replace(key, worker);
        return "Old element under the key = " + key + ":\n" +
               oldWorker + "\n" +
               "was replaced by a new one:\n" +
               worker + "\n";
    }

//    @Deprecated
//    public static HashMap<Long, Worker> tempWorkerMap() {
//        HashMap<Long, Worker> map = new HashMap<>();
//        Worker one = new Worker(1, "One", new Coordinates(1, 2D), LocalDate.now(), 9000,
//                LocalDate.now(), Position.DEVELOPER, Status.HIRED,
//                new Organization("Company", 150000, 200L,
//                        new Address("Street", "1111")));
//        Worker two = new Worker(2, "Two", new Coordinates(1, 2D), LocalDate.now(), 10000,
//                LocalDate.now(), Position.MANAGER_OF_CLEANING, Status.RECOMMENDED_FOR_PROMOTION,
//                new Organization("Company", 100000, 200L,
//                        new Address("Street", "1111")));
//        Worker three = new Worker(3, "Three", new Coordinates(1, 2D), LocalDate.now(), 8000,
//                LocalDate.now(), Position.HEAD_OF_DEPARTMENT, Status.REGULAR,
//                new Organization("Company", 130000, 200L,
//                        new Address("Street", "1111")));
//        Worker four = new Worker(4, "Four", new Coordinates(1, 2D), LocalDate.now(), 7000,
//                LocalDate.now(), Position.DEVELOPER, Status.REGULAR,
//                new Organization("Company", 100001, 200L,
//                        new Address("Street", "1111")));
//        Worker five = new Worker(5, "Five", new Coordinates(1, 2D), LocalDate.now(), 14000,
//                LocalDate.now(), Position.MANAGER_OF_CLEANING, Status.FIRED,
//                new Organization("Company", 160000, 200L,
//                        new Address("Street", "1111")));
//        map.put(one.getId(), one);
//        map.put(two.getId(), two);
//        map.put(three.getId(), three);
//        map.put(four.getId(), four);
//        map.put(five.getId(), five);
//        return map;
//    }
}
