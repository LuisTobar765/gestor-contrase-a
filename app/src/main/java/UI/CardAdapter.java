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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Model.passApps;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<passApps> cardList;
    private Context context;
    private DatabaseReference databaseReference;

    public CardAdapter(ArrayList<passApps> cardList, Context context) {
        this.cardList = cardList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("passApps");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        passApps card = cardList.get(position);

        holder.etAppName.setText(card.getAppName());
        holder.etUserName.setText(card.getUserName());
        holder.etUserEmail.setText(card.getEmail());

        holder.btnEdit.setOnClickListener(v -> {
            if (context instanceof Home) {
                ((Home) context).showEditCardPopup(card);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            databaseReference.child(card.getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al eliminar la tarjeta", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView etAppName, etUserName, etUserEmail;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etAppName = itemView.findViewById(R.id.etappName);
            etUserName = itemView.findViewById(R.id.etuserName);
            etUserEmail = itemView.findViewById(R.id.etuserEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
