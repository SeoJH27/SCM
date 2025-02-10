package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.Cafeteria
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding

class TodayMenuAdapter(
    private val item: List<Meal>,
) : RecyclerView.Adapter<TodayMenuItemViewHolder>() {

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayMenuItemViewHolder {
        return TodayMenuItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: TodayMenuItemViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}

class TodayMenuItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(meal: List<Meal>) {
        with(binding) {
            meal.forEach {
                txtTime.setText(it.mealType)
                txtMenu.setText(it.mainMenu + it.subMenu)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): TodayMenuItemViewHolder {
            return TodayMenuItemViewHolder(
                ItemDetailMenuBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}