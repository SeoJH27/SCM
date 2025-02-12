package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.Cafeteria
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.data.TodayMenu
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding

class TodayMenuAdapter(
    private val items: TodayMenu,
    private val restaurantName: String,
) : RecyclerView.Adapter<TodayMenuItemViewHolder>() {

    private lateinit var MEAL: List<Meal>

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayMenuItemViewHolder {
        items.restaurants.forEach {
            if(it.restaurantName == restaurantName){
                MEAL = it.meals
            }
        }
        return TodayMenuItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: TodayMenuItemViewHolder, position: Int) {

        // TODO: **아이템 수를 늘리며 추가** -> 우선순위
        holder.bind(MEAL)
    }

    override fun getItemCount(): Int { return MEAL.size }

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