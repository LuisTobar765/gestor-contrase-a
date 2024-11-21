package UI;

import android.os.Bundle;
import android.util.Base64;
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
import java.util.UUID;

import Model.passApps;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddCard;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<passApps> cardList;
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

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

        // Cargar tarjetas del usuario
        loadUserCards();

        // Acción del botón para agregar tarjeta
        btnAddCard.setOnClickListener(v -> showAddCardPopup());
    }

    private void loadUserCards() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("passApps");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    passApps card = dataSnapshot.getValue(passApps.class);
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

        // Acción del botón Guardar
        btnSaveCard.setOnClickListener(v -> {
            String appName = etAppName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String userName = etUserName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();
            String userEmail = mAuth.getCurrentUser().getEmail();

            // Validar campos obligatorios
            if (appName.isEmpty() || email.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Cifrar la contraseña
                String secretKey = "1234567890123456"; // Clave de 16 caracteres
                String encryptedPassword = EncryptionUtil.encrypt(password, secretKey);

                // Generar un ID único para la tarjeta
                String Id = UUID.randomUUID().toString();

                // Crear objeto passApps
                passApps newCard = new passApps(Id, appName, email, userName, encryptedPassword, notas, userEmail);

                // Guardar en Firebase
                databaseReference.child(Id).setValue(newCard)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Tarjeta guardada correctamente", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Log.e("FirebaseError", "Error al guardar la tarjeta: " + task.getException());
                                Toast.makeText(this, "Error al guardar la tarjeta", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Error al cifrar la contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
