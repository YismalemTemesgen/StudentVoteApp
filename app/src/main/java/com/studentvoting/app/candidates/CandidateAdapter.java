package com.studentvoting.app.candidates;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.studentvoting.app.R;
import com.studentvoting.app.database.Candidate;

import java.util.ArrayList;
import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    public interface OnCandidateClickListener {
        void onCandidateClick(Candidate candidate);
    }

    private List<Candidate> candidates = new ArrayList<>();
    private final Context context;
    private OnCandidateClickListener listener;
    private boolean showDeleteButton = false;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Candidate candidate);
    }

    // Avatar background colors cycling
    private static final int[] AVATAR_COLORS = {
            0xFF3F51B5, 0xFF0288D1, 0xFF00897B, 0xFF43A047,
            0xFFE91E63, 0xFF8E24AA, 0xFFFF6F00, 0xFF6D4C41
    };

    public CandidateAdapter(Context context) {
        this.context = context;
    }

    public void setOnCandidateClickListener(OnCandidateClickListener listener) {
        this.listener = listener;
    }

    public void setShowDeleteButton(boolean show, OnDeleteClickListener deleteListener) {
        this.showDeleteButton = show;
        this.deleteListener = deleteListener;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates != null ? candidates : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);
        holder.bind(candidate, position);
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    class CandidateViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvInitial;
        private final TextView tvName;
        private final TextView tvPosition;
        private final TextView tvDepartment;
        private final TextView tvYear;
        private final TextView tvVoteCount;
        private final TextView tvCandidateId;
        private final View btnDelete;

        CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_candidate);
            tvInitial = itemView.findViewById(R.id.tv_initial);
            tvName = itemView.findViewById(R.id.tv_candidate_name);
            tvPosition = itemView.findViewById(R.id.tv_candidate_position);
            tvDepartment = itemView.findViewById(R.id.tv_candidate_department);
            tvYear = itemView.findViewById(R.id.tv_candidate_year);
            tvVoteCount = itemView.findViewById(R.id.tv_vote_count);
            tvCandidateId = itemView.findViewById(R.id.tv_candidate_id);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        void bind(Candidate candidate, int position) {
            // Set initial avatar
            String initial = candidate.getPhotoInitial() != null && !candidate.getPhotoInitial().isEmpty()
                    ? candidate.getPhotoInitial().substring(0, 1).toUpperCase()
                    : candidate.getName().substring(0, 1).toUpperCase();
            tvInitial.setText(initial);

            // Set avatar background color
            int colorIndex = position % AVATAR_COLORS.length;
            tvInitial.getBackground().setTint(AVATAR_COLORS[colorIndex]);

            tvName.setText(candidate.getName());
            tvPosition.setText(candidate.getPosition());
            tvDepartment.setText(candidate.getDepartment());
            tvYear.setText(candidate.getYear());
            tvVoteCount.setText(candidate.getVoteCount() + " votes");
            tvCandidateId.setText("ID: " + candidate.getId());

            // Delete button visibility
            if (btnDelete != null) {
                btnDelete.setVisibility(showDeleteButton ? View.VISIBLE : View.GONE);
                if (showDeleteButton && deleteListener != null) {
                    btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(candidate));
                }
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCandidateClick(candidate);
                }
            });

            // Animate item
            itemView.setAlpha(0f);
            itemView.setTranslationY(30f);
            itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(position * 50L)
                    .start();
        }
    }
}
