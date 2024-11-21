package UI;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luistobar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Model.passApps;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private ArrayList<passApps> cardList;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userEmail;

    // Clave de cifrado (debe ser de 16 caracteres)
    private static final String ENCRYPTION_KEY = "1234567890123456";

    private Executor executor;
    private passApps currentEditingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("passApps");
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList, this);
        recyclerView.setAdapter(cardAdapter);

        loadCards(); // Cargar las tarjetas desde Firebase

        executor = Executors.newSingleThreadExecutor();

        // Botón para agregar nuevas contraseñas
        Button btnAddPassword = findViewById(R.id.btnAddCard);
        btnAddPassword.setOnClickListener(v -> showAddPasswordPopup());

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            // Cerrar sesión
            FirebaseAuth.getInstance().signOut();

            // Redirigir al usuario a la pantalla de inicio de sesión
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish(); // Finalizar la actividad actual para evitar que el usuario regrese a la pantalla principal con el botón de atrás
        });
    }

    // Método para cargar las tarjetas desde Firebase
    private void loadCards() {
        databaseReference.orderByChild("userEmail").equalTo(userEmail)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cardList.clear(); // Limpiar la lista antes de agregar nuevos datos
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            passApps card = snapshot.getValue(passApps.class);
                            if (card != null) {
                                cardList.add(card);
                            }
                        }
                        cardAdapter.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Home.this, "Error al cargar datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Mostrar el cuadro de diálogo para agregar una nueva tarjeta
    private void showAddPasswordPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_card, null);
        dialogBuilder.setView(popupView);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        EditText etAppName = popupView.findViewById(R.id.etAppName);
        EditText etEmail = popupView.findViewById(R.id.etEmail);
        EditText etUserName = popupView.findViewById(R.id.etUserName);
        EditText etPassword = popupView.findViewById(R.id.etPassword);
        EditText etNotas = popupView.findViewById(R.id.etNotas);
        Button btnSave = popupView.findViewById(R.id.btnSaveCard);
        Button btnCancel = popupView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String appName = etAppName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String userName = etUserName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();

            if (appName.isEmpty() || email.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String encryptedPassword = EncryptionUtil.encrypt(password, ENCRYPTION_KEY);
                String id = UUID.randomUUID().toString();
                passApps newCard = new passApps(id, appName, email, userName, encryptedPassword, notas, userEmail);

                databaseReference.child(id).setValue(newCard).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Tarjeta agregada correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error al guardar la tarjeta", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cifrar la contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostrar el cuadro de edición (requiere autenticación)
    public void showEditCardPopup(passApps card) {
        if (isBiometricSupported()) {
            currentEditingCard = card;
            promptBiometricAuthentication();
        } else {
            currentEditingCard = card;
            promptKeyguardAuthentication();
        }
    }

    private boolean isBiometricSupported() {
        BiometricManager biometricManager = BiometricManager.from(this);
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    private void promptBiometricAuthentication() {
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                showEditCardPopupAfterAuth(currentEditingCard);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Home.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Home.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación requerida")
                .setSubtitle("Autentícate para editar esta tarjeta")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void promptKeyguardAuthentication() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(
                    "Autenticación requerida", "Autentícate para editar esta tarjeta"
            );
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "Configura un método de desbloqueo seguro", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            showEditCardPopupAfterAuth(currentEditingCard);
        } else {
            Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditCardPopupAfterAuth(passApps card) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_edit_card, null);
        dialogBuilder.setView(popupView);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        EditText etAppName = popupView.findViewById(R.id.etAppName);
        EditText etEmail = popupView.findViewById(R.id.etEmail);
        EditText etUserName = popupView.findViewById(R.id.etUserName);
        EditText etPassword = popupView.findViewById(R.id.etPassword);
        EditText etNotas = popupView.findViewById(R.id.etNotas);
        Button btnSaveChanges = popupView.findViewById(R.id.btnSaveCard);
        Button btnCancel = popupView.findViewById(R.id.btnCancel);

        etAppName.setText(card.getAppName());
        etEmail.setText(card.getEmail());
        etUserName.setText(card.getUserName());

        try {
            etPassword.setText(EncryptionUtil.decrypt(card.getPassword(), ENCRYPTION_KEY));
        } catch (Exception e) {
            etPassword.setText("Error al descifrar");
        }

        etNotas.setText(card.getNotas());

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSaveChanges.setOnClickListener(v -> {
            String appName = etAppName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String userName = etUserName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();

            if (appName.isEmpty() || email.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String encryptedPassword = EncryptionUtil.encrypt(password, ENCRYPTION_KEY);

                card.setAppName(appName);
                card.setEmail(email);
                card.setUserName(userName);
                card.setPassword(encryptedPassword);
                card.setNotas(notas);

                databaseReference.child(card.getId()).setValue(card).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Tarjeta actualizada correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error al actualizar la tarjeta", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cifrar la contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
