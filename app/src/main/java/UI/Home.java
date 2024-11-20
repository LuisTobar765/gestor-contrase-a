package UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luistobar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import Model.passApp;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddCard;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<passApp> cardList;
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.home);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("passApp");

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        btnAddCard = findViewById(R.id.btnAddCard);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList, this);
        recyclerView.setAdapter(cardAdapter);

        // Cargar tarjetas
        loadUserCards();

        // Botón agregar tarjeta
        btnAddCard.setOnClickListener(v -> showAddCardPopup());
    }

    private void loadUserCards() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    passApp card = dataSnapshot.getValue(passApp.class);
                    if (card != null && card.getUserEmail().equals(currentUserEmail)) {
                        cardList.add(card);
                    }
                }
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCardPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_card, null);
        dialogBuilder.setView(popupView);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Referencias a los campos
        EditText etAppName = popupView.findViewById(R.id.etAppName);
        EditText etEmail = popupView.findViewById(R.id.etEmail);
        EditText etUserName = popupView.findViewById(R.id.etUserName);
        EditText etPassword = popupView.findViewById(R.id.etPassword);
        EditText etNotas = popupView.findViewById(R.id.etNotas);
        Button btnSaveCard = popupView.findViewById(R.id.btnSaveCard);

        btnSaveCard.setOnClickListener(v -> {
            String appName = etAppName.getText().toString();
            String email = etEmail.getText().toString();
            String userName = etUserName.getText().toString();
            String password = etPassword.getText().toString();
            String notas = etNotas.getText().toString();
            String userEmail = mAuth.getCurrentUser().getEmail();

            if (appName.isEmpty() || email.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("passApps");

// Escucha para leer los datos
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Intenta convertir el snapshot a un objeto passApp
                        passApp passAppData = dataSnapshot.getValue(passApp.class);

                        if (passAppData != null) {
                            // Usa los datos de passApp
                            Log.d("FirebaseData", "App Name: " + passAppData.getAppName());
                            Log.d("FirebaseData", "Email: " + passAppData.getEmail());
                            Log.d("FirebaseData", "Username: " + passAppData.getUserName());
                        } else {
                            Log.e("FirebaseData", "Los datos no coinciden con el modelo passApp.");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error al leer datos: " + error.getMessage());
                }
            });

        });
    }
}
