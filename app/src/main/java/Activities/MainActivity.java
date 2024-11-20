package Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luistobar.R;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        // Referencia al botón "Ingresar"
        MaterialButton btnIngresar = findViewById(R.id.btnIngresar);

        // Configurar listener para el botón
        btnIngresar.setOnClickListener(v -> handleLogin());
    }

    // Método para manejar la acción del botón
    private void handleLogin() {
        // Aquí podrías realizar validaciones o navegar a otra actividad
        Toast.makeText(this, "¡Ingresando al Gestor de Contraseña!", Toast.LENGTH_SHORT).show();

        // Simulación de redirección
        // startActivity(new Intent(MainActivity.this, DashboardActivity.class));
    }
}
