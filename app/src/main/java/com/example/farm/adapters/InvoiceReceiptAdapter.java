package com.example.farm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farm.R;
import com.example.farm.models.InvoiceReceipt;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class InvoiceReceiptAdapter extends FirestoreRecyclerAdapter<InvoiceReceipt, InvoiceReceiptAdapter.InvoiceReceiptHolder> {
private Context context;

    public InvoiceReceiptAdapter(@NonNull FirestoreRecyclerOptions<InvoiceReceipt> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull InvoiceReceiptHolder holder, int position, @NonNull InvoiceReceipt invoiceReceipt) {
        holder.textViewCategory.setText(invoiceReceipt.getCategory());
        holder.textViewAmount.setText(String.valueOf(invoiceReceipt.getAmount()));
        holder.textViewDate.setText(invoiceReceipt.getDate());
        String invoiceImageUrl = invoiceReceipt.getImage();
        holder.setImage(invoiceImageUrl);
    }

    @NonNull
    @Override
    public InvoiceReceiptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.invoice_receipt_rows, parent, false);
        context=parent.getContext();
        return new InvoiceReceiptHolder(v);
    }

    class InvoiceReceiptHolder extends RecyclerView.ViewHolder{
        TextView  textViewCategory, textViewAmount, textViewDate;
        ImageView image;

        InvoiceReceiptHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            image = itemView.findViewById(R.id.invoiceReceiptImage);
        }

        private void setImage(String invoiceImageUrl)
        {
            image = itemView.findViewById(R.id.invoiceReceiptImage);

            RequestOptions placeholderOption= new RequestOptions();
            //Preconditions.checkNotNull(mContext); -- this is throwing null pointer exception
            if (context!= null) {
                Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(invoiceImageUrl).into(image);
            }
        }
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();

    }
}
