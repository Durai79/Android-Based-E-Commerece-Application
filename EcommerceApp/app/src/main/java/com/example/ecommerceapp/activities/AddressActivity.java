package com.example.ecommerceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.adapters.AddressAdapter;
import com.example.ecommerceapp.models.AddressModel;
import com.example.ecommerceapp.models.MyCartModel;
import com.example.ecommerceapp.models.NewProductsModel;
import com.example.ecommerceapp.models.PopularProductModel;
import com.example.ecommerceapp.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress{

    Button addAddress;
    RecyclerView recyclerView;
    Button addAddressBtn,paymentBtn;
    Toolbar toolbar;
    String mAddress = "";

    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;

    FirebaseFirestore firestore;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getWindow().setStatusBarColor(ContextCompat.getColor(AddressActivity.this, R.color.statusbarcolor));

        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get data from detailed acticity
        Object obj = getIntent().getSerializableExtra("item");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.address_recycler);
        paymentBtn = findViewById(R.id.payment_btn);
        addAddress = findViewById(R.id.add_address_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(addressModelList, this, this);
        recyclerView.setAdapter(addressAdapter);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                                addressAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double amount = 0.0;
                if (obj instanceof NewProductsModel){
                    NewProductsModel newProductsModel = (NewProductsModel) obj;
                    amount = newProductsModel.getPrice();
                }
                if (obj instanceof PopularProductModel){
                    PopularProductModel popularProductModel = (PopularProductModel) obj;
                    amount = popularProductModel.getPrice();
                }
                if (obj instanceof ShowAllModel){
                    ShowAllModel showAllModel = (ShowAllModel) obj;
                    amount = showAllModel.getPrice();
                }
                Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                intent.putExtra("amount",amount);
                startActivity(intent);
            }
        });


        addAddress = findViewById(R.id.add_address_btn);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressActivity.this, AddAddressActivity.class));
            }
        });

    }

    @Override
    public void setAddress(String address) {

        mAddress = address;
    }
}