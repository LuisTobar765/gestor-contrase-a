package UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luistobar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Model.passApps;

public class PassAppAdapter extends RecyclerView.Adapter<PassAppAdapter.PassAppViewHolder> {

    private Context context;
    private ArrayList<passApps> passAppList;

    public PassAppAdapter(Context context, ArrayList<passApps> passAppList) {
        this.context = context;
        this.passAppList = passAppList;
    }

    @NonNull
    @Override
    public PassAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new PassAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassAppViewHolder holder, int position) {
        passApps card = passAppList.get(position);
        holder.appName.setText(card.getAppName());
        holder.userName.setText(card.getEmail());
        holder.userEmail.setText(card.getUserEmail());

        // Botón Editar
        holder.btnEdit.setOnClickListener(v -> {
            // Implementar el método para mostrar un diálogo de edición
            Toast.makeText(context, "Editar: " + card.getAppName(), Toast.LENGTH_SHORT).show();
        });

        // Botón Eliminar
        holder.btnDelete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance()
                    .getReference("cards")
                    .child(card.getId()) // ID único de la tarjeta
                    .removeValue()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return passAppList.size();
    }

    public static class PassAppViewHolder extends RecyclerView.ViewHolder {

        TextView appName, userName, userEmail;
        Button btnEdit, btnDelete;

        public PassAppViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById( R.id.etappName);
            userName = itemView.findViewById(R.id.etuserName);
            userEmail = itemView.findViewById(R.id.etuserEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
