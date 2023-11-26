package com.rag.foodMeMiaAdmin.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ListenerUtil {
    public static void onClickBtnIntent(View view, Context context, Class<?> destinationActivity ){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,destinationActivity);
                context.startActivity(intent);
            }
        });

    }
}
