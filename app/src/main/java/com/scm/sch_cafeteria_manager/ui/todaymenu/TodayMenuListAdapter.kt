package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.data.TodayMenu
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak
import com.scm.sch_cafeteria_manager.ui.detail.hs1.DetailHs1ItemClickListener

class TodayMenuListAdapter(
    items: TodayMenu,
    private val restaurantName: String,
    private val listener: TodayMenuItemClickListener
) : RecyclerView.Adapter<TodayMenuItemViewHolder>() {

    private var MEAL: List<Meal> = emptyList()

    init {
        items.restaurants.forEach {
            if (it.restaurantName == restaurantName) {
                MEAL = it.meals
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayMenuItemViewHolder {
        if (MEAL.isEmpty()) {
            Log.e("TodayMenuListAdapter", "데이터가 없습니다.")
        }
        return TodayMenuItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: TodayMenuItemViewHolder, position: Int) {
        holder.bind(MEAL.get(position), listener)
    }

    override fun getItemCount(): Int = MEAL.size
}

class TodayMenuItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(meal: Meal, listener: TodayMenuItemClickListener) {
        itemView.setOnClickListener {
            listener.onTodayMenuClick(meal)
        }
        with(binding) {
            txtTime.text = meal.mealType
            txtMenu.text =
                meal.mainMenu.replaceCommaToLinebreak() + "\n" + meal.subMenu.replaceCommaToLinebreak()
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