package com.example.petcareproject.utils;

import com.example.petcareproject.models.Application;
import com.example.petcareproject.models.CareRequest;
import com.example.petcareproject.models.Pet;
import com.example.petcareproject.models.Review;
import com.example.petcareproject.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FakeDataManager {

    private static final List<User> users = new ArrayList<>();
    private static final List<Pet> pets = new ArrayList<>();
    private static final List<CareRequest> requests = new ArrayList<>();
    private static final List<Application> applications = new ArrayList<>();
    private static final List<Review> reviews = new ArrayList<>();

    // Fake logged-in user ids for frontend testing
    public static String currentOwnerId = "owner_1";
    public static String currentSitterId = "sitter_1";

    static {
        initFakeData();
    }

    private static void initFakeData() {
        // Owners
        users.add(new User("owner_1", "Sami Ben Ali", "sami@mail.com", "22 123 456", "Tunis",
                Constants.ROLE_OWNER, "I have two pets and I travel often."));
        users.add(new User("owner_2", "Rania Mansouri", "rania@mail.com", "25 654 321", "Ariana",
                Constants.ROLE_OWNER, "Dog lover looking for reliable neighbors."));

        // Sitters
        users.add(new User("sitter_1", "Yasmine Trabelsi", "yasmine@mail.com", "55 123 456", "Sousse",
                Constants.ROLE_SITTER, "I love animals and I have a garden.",
                Arrays.asList("Cat", "Dog"), 12, 4.9, Constants.BADGE_TRUSTED));
        users.add(new User("sitter_2", "Amine Khelifi", "amine@mail.com", "50 222 333", "Tunis",
                Constants.ROLE_SITTER, "Experienced with large dogs.",
                Collections.singletonList("Dog"), 9, 4.8, Constants.BADGE_TRUSTED));
        users.add(new User("sitter_3", "Rania M.", "raniam@mail.com", "52 444 555", "Ariana",
                Constants.ROLE_SITTER, "Calm home, great for cats and rabbits.",
                Arrays.asList("Cat", "Rabbit"), 5, 4.6, Constants.BADGE_CONFIRMED));
        users.add(new User("sitter_4", "Walid S.", "walid@mail.com", "53 666 777", "Sfax",
                Constants.ROLE_SITTER, "Available on weekends and holidays.",
                Collections.singletonList("Dog"), 3, 4.5, Constants.BADGE_CONFIRMED));
        users.add(new User("sitter_5", "Lina H.", "lina@mail.com", "54 888 999", "Tunis",
                Constants.ROLE_SITTER, "First time sitting, very motivated!",
                Collections.singletonList("Cat"), 0, 0.0, Constants.BADGE_NEW));

        // Pets
        pets.add(new Pet("pet_1", "owner_1", "Luna", "Cat", "Siamese", 2, "Indoor cat only"));
        pets.add(new Pet("pet_2", "owner_1", "Max", "Dog", "Labrador", 4, "No chicken"));
        pets.add(new Pet("pet_3", "owner_2", "Coco", "Bird", "Parrot", 1, "Fresh water daily"));
        pets.add(new Pet("pet_4", "owner_2", "Rex", "Dog", "German Shepherd", 3, "Needs 2 walks per day"));

        // Care Requests
        requests.add(new CareRequest("req_1", "owner_1", "pet_1", "Luna", "Cat", "Tunis",
                "15/11/2026", "22/11/2026", 20.0,
                "Looking for someone to feed Luna and play with her.", Constants.STATUS_OPEN, null));
        requests.add(new CareRequest("req_2", "owner_1", "pet_2", "Max", "Dog", "Ariana",
                "16/11/2026", "20/11/2026", 25.0,
                "Going on a trip, Max needs two walks per day.", Constants.STATUS_OPEN, null));
        requests.add(new CareRequest("req_3", "owner_2", "pet_3", "Coco", "Bird", "Sousse",
                "18/11/2026", "25/11/2026", 15.0,
                "Need someone to look after Coco at home.", Constants.STATUS_OPEN, null));
        requests.add(new CareRequest("req_4", "owner_2", "pet_4", "Rex", "Dog", "Sfax",
                "20/11/2026", "27/11/2026", 30.0,
                "Rex is very friendly and loves playing outside.", Constants.STATUS_OPEN, null));
        requests.add(new CareRequest("req_5", "owner_1", "pet_1", "Luna", "Cat", "Tunis",
                "01/12/2026", "05/12/2026", 20.0,
                "Short weekend trip.", Constants.STATUS_CONFIRMED, "sitter_1"));
        requests.add(new CareRequest("req_6", "owner_1", "pet_2", "Max", "Dog", "Tunis",
                "10/10/2026", "14/10/2026", 25.0,
                "Completed autumn stay.", Constants.STATUS_COMPLETED, "sitter_1"));
        requests.add(new CareRequest("req_7", "owner_1", "pet_1", "Luna", "Cat", "Tunis",
                "02/09/2026", "06/09/2026", 18.0,
                "Trip was cancelled.", Constants.STATUS_CANCELLED, null));

        // Applications
        applications.add(new Application("app_1", "req_2", "sitter_1", "Yasmine Trabelsi",
                "I am available all week, I have a garden.", Constants.APP_PENDING));
        applications.add(new Application("app_2", "req_2", "sitter_2", "Amine Khelifi",
                "I have taken care of Labradors before.", Constants.APP_PENDING));
        applications.add(new Application("app_3", "req_2", "sitter_5", "Lina H.",
                "First stay, very motivated!", Constants.APP_PENDING));
        applications.add(new Application("app_4", "req_5", "sitter_1", "Yasmine Trabelsi",
                "Happy to watch Luna again!", Constants.APP_ACCEPTED));
        applications.add(new Application("app_5", "req_3", "sitter_1", "Yasmine Trabelsi",
                "I live in Sousse and love birds.", Constants.APP_PENDING));

        // Reviews
        reviews.add(new Review("rev_1", "req_6", "owner_1", "sitter_1", 5,
                "Very attentive with Max."));
        reviews.add(new Review("rev_2", "req_6", "owner_2", "sitter_1", 4,
                "Punctual and reliable."));
    }

    // TODO Firebase: query careRequests where status == "open"
    public static List<CareRequest> getOpenRequests() {
        List<CareRequest> result = new ArrayList<>();
        for (CareRequest r : requests) {
            if (Constants.STATUS_OPEN.equals(r.getStatus())) {
                result.add(r);
            }
        }
        return result;
    }

    // TODO Firebase: query careRequests where ownerId == currentUserId
    public static List<CareRequest> getOwnerRequests(String ownerId) {
        List<CareRequest> result = new ArrayList<>();
        for (CareRequest r : requests) {
            if (r.getOwnerId().equals(ownerId)) {
                result.add(r);
            }
        }
        return result;
    }

    // TODO Firebase: query careRequests where ownerId == currentUserId and status == "open"
    public static List<CareRequest> getOwnerOpenRequests(String ownerId) {
        List<CareRequest> result = new ArrayList<>();
        for (CareRequest r : requests) {
            if (r.getOwnerId().equals(ownerId) && Constants.STATUS_OPEN.equals(r.getStatus())) {
                result.add(r);
            }
        }
        return result;
    }

    // TODO Firebase: get document careRequests/{requestId}
    public static CareRequest getRequestById(String requestId) {
        for (CareRequest r : requests) {
            if (r.getRequestId().equals(requestId)) {
                return r;
            }
        }
        return null;
    }

    // TODO Firebase: get document pets/{petId}
    public static Pet getPetById(String petId) {
        for (Pet p : pets) {
            if (p.getPetId().equals(petId)) {
                return p;
            }
        }
        return null;
    }

    // TODO Firebase: query pets where ownerId == currentUserId
    public static List<Pet> getPetsByOwner(String ownerId) {
        List<Pet> result = new ArrayList<>();
        for (Pet p : pets) {
            if (p.getOwnerId().equals(ownerId)) {
                result.add(p);
            }
        }
        return result;
    }

    // TODO Firebase: get document users/{userId}
    public static User getUserById(String userId) {
        for (User u : users) {
            if (u.getUserId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    // TODO Firebase: query applications where sitterId == currentUserId
    public static List<Application> getApplicationsBySitter(String sitterId) {
        List<Application> result = new ArrayList<>();
        for (Application a : applications) {
            if (a.getSitterId().equals(sitterId)) {
                result.add(a);
            }
        }
        return result;
    }

    // TODO Firebase: query applications where requestId == id
    public static List<Application> getApplicationsForRequest(String requestId) {
        List<Application> result = new ArrayList<>();
        for (Application a : applications) {
            if (a.getRequestId().equals(requestId)) {
                result.add(a);
            }
        }
        return result;
    }

    // TODO Firebase: query users where role == "sitter" ordered by averageRating desc
    public static List<User> getTopSitters() {
        List<User> sitters = new ArrayList<>();
        for (User u : users) {
            if (Constants.ROLE_SITTER.equals(u.getRole())) {
                sitters.add(u);
            }
        }
        Collections.sort(sitters, new Comparator<User>() {
            @Override
            public int compare(User a, User b) {
                return Double.compare(b.getAverageRating(), a.getAverageRating());
            }
        });
        return sitters;
    }

    // TODO Firebase: query reviews where sitterId == id
    public static List<Review> getReviewsForSitter(String sitterId) {
        List<Review> result = new ArrayList<>();
        for (Review r : reviews) {
            if (r.getSitterId().equals(sitterId)) {
                result.add(r);
            }
        }
        return result;
    }

    public static User getUserByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public static boolean hasSitterApplied(String requestId, String sitterId) {
        for (Application a : applications) {
            if (a.getRequestId().equals(requestId) && a.getSitterId().equals(sitterId)) {
                return true;
            }
        }
        return false;
    }

    // TODO Firebase: add document in applications collection
    public static void addApplication(String requestId, String sitterId, String message) {
        User sitter = getUserById(sitterId);
        String sitterName = sitter != null ? sitter.getFullName() : "Sitter";
        String id = "app_" + (applications.size() + 1);
        applications.add(new Application(id, requestId, sitterId, sitterName, message, Constants.APP_PENDING));
    }

    // TODO Firebase: WriteBatch -> accept chosen application, reject others, confirm request
    public static void chooseSitter(String requestId, String applicationId) {
        String chosenSitterId = null;
        for (Application a : applications) {
            if (a.getRequestId().equals(requestId)) {
                if (a.getApplicationId().equals(applicationId)) {
                    a.setStatus(Constants.APP_ACCEPTED);
                    chosenSitterId = a.getSitterId();
                } else {
                    a.setStatus(Constants.APP_REJECTED);
                }
            }
        }
        CareRequest req = getRequestById(requestId);
        if (req != null) {
            req.setStatus(Constants.STATUS_CONFIRMED);
            req.setChosenSitterId(chosenSitterId);
        }
    }

    // TODO Firebase: WriteBatch -> set request status = cancelled, reject pending applications
    public static void cancelRequest(String requestId) {
        CareRequest req = getRequestById(requestId);
        if (req != null) {
            req.setStatus(Constants.STATUS_CANCELLED);
        }
        for (Application a : applications) {
            if (a.getRequestId().equals(requestId) && Constants.APP_PENDING.equals(a.getStatus())) {
                a.setStatus(Constants.APP_REJECTED);
            }
        }
    }

    // TODO Firebase: add or update document in pets collection
    public static void savePet(Pet pet) {
        if (pet.getPetId() == null || pet.getPetId().isEmpty()) {
            pet.setPetId("pet_" + (pets.size() + 1));
            pets.add(pet);
        } else {
            for (int i = 0; i < pets.size(); i++) {
                if (pets.get(i).getPetId().equals(pet.getPetId())) {
                    pets.set(i, pet);
                    return;
                }
            }
            pets.add(pet);
        }
    }

    // TODO Firebase: add document in careRequests collection
    public static void addCareRequest(CareRequest request) {
        request.setRequestId("req_" + (requests.size() + 1));
        requests.add(0, request);
    }

    // TODO Firebase: Transaction -> complete request, add review, update sitter stats & badge
    public static void completeRequest(String requestId, String ownerId, int rating, String comment) {
        CareRequest req = getRequestById(requestId);
        if (req == null) return;

        req.setStatus(Constants.STATUS_COMPLETED);
        String sitterId = req.getChosenSitterId();
        if (sitterId != null) {
            String revId = "rev_" + (reviews.size() + 1);
            reviews.add(new Review(revId, requestId, ownerId, sitterId, rating, comment));

            User sitter = getUserById(sitterId);
            if (sitter != null) {
                int oldCount = sitter.getCompletedCount();
                double oldAvg = sitter.getAverageRating();
                int newCount = oldCount + 1;
                double newAvg = (oldAvg * oldCount + rating) / newCount;
                sitter.setCompletedCount(newCount);
                sitter.setAverageRating(Math.round(newAvg * 10.0) / 10.0);

                if (newCount >= 8 && newAvg >= 4.5) {
                    sitter.setBadge(Constants.BADGE_TRUSTED);
                } else if (newCount >= 3) {
                    sitter.setBadge(Constants.BADGE_CONFIRMED);
                } else {
                    sitter.setBadge(Constants.BADGE_NEW);
                }
            }
        }
    }

    public static long calculateDays(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            if (start != null && end != null) {
                long diff = end.getTime() - start.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                return Math.max(days, 1);
            }
        } catch (ParseException ignored) {
        }
        return 1;
    }
}
