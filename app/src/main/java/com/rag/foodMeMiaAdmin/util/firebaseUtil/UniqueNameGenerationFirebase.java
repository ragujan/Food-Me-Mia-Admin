package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static com.rag.foodMeMiaAdmin.util.Constants.FOOD_IMAGE_FOLDER_PATH;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UniqueNameGenerationFirebase {

    public static Single<String> getUniqueImageName() {
        return Single.<String>create(emitter -> {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference listRef = storage.getReference().child(FOOD_IMAGE_FOLDER_PATH);

                    String generatedName = generateString();
                    listRef.listAll()
                            .addOnSuccessListener(listResult -> {
                                List<String> names = new LinkedList<>();

                                for (StorageReference item : listResult.getItems()) {

                                    names.add(item.getName());
                                }

                                boolean isNameUnique = true;

                                String newName = generateString();
                                if (names.contains(newName)) {
                                    isNameUnique = false;
                                }
                                while (!isNameUnique) {
                                    if (names.contains(newName)) {
                                        newName = generateString();
                                    } else {
                                        isNameUnique = true;
                                    }
                                }
                                emitter.onSuccess(newName);
                            })
                            .addOnFailureListener(emitter::onError);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<String> observeUniqueName() {

        Single<String> single = getUniqueImageName();

        return single;
    }

    private static String generateString() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        int n = 8;
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}
