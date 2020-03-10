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
import com.example.farm.invoiceExpenses.InvoiceExpensesActivity;
import com.example.farm.models.InvoiceExpense;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class InvoiceExpenseAdapter extends FirestoreRecyclerAdapter<InvoiceExpense, InvoiceExpenseAdapter.InvoiceExpenseHolder> {
Context context;

    public InvoiceExpenseAdapter(@NonNull FirestoreRecyclerOptions<InvoiceExpense> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull InvoiceExpenseAdapter.InvoiceExpenseHolder holder, int position, @NonNull InvoiceExpense invoiceExpense) {
        holder.textViewInvoiceType.setText(invoiceExpense.getInvoiceType());
        holder.textViewCategory.setText(invoiceExpense.getCategory());

        String invoiceImageUrl = invoiceExpense.getImage();
        holder.setInvoiceImage(invoiceImageUrl);
    }

    @NonNull
    @Override
    public InvoiceExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.invoice_expense_rows, parent, false);
        context=parent.getContext();
        return new InvoiceExpenseHolder(v);
    }

    class InvoiceExpenseHolder extends RecyclerView.ViewHolder{
        TextView textViewInvoiceType, textViewCategory;
        ImageView invoiceImage;

        InvoiceExpenseHolder(@NonNull View itemView) {
            super(itemView);

            textViewInvoiceType = itemView.findViewById(R.id.textViewInvoiceType);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            invoiceImage = itemView.findViewById(R.id.invoiceExpenseImage);
        }

        public void setInvoiceImage(String invoiceImageUrl)
        {
            invoiceImage = itemView.findViewById(R.id.invoiceExpenseImage);

            RequestOptions placeholderOption= new RequestOptions();
            //Preconditions.checkNotNull(mContext); -- this is throwing null pointer exception
            if (context!= null) {
                Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(invoiceImageUrl).into(invoiceImage);
            }
        }
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();

    }
}
