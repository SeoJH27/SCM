package com.scm.sch_cafeteria_manager.ui.detail.hs1

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.DetailMenu
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak

class DetailHs1ListAdapter(
    items: DetailMenu,
    private val dayOfWeek: String,
    private val listener: DetailHs1ItemClickListener
) : RecyclerView.Adapter<DetailHs1ItemViewHolder>() {

    private var MEAL: List<Meal> = emptyList()

    init {
        items.weekMealPlans.dailyMealPlans.forEach {
            if (it.dayOfWeek == dayOfWeek) {
                MEAL = it.meals
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHs1ItemViewHolder {
        if (MEAL.isEmpty()) {
            Log.e("DetailHs1ListAdapter", "데이터가 없습니다.")
        }

        return DetailHs1ItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: DetailHs1ItemViewHolder, position: Int) {
        holder.bind(
            MEAL.get(position), listener
        )
    }

    override fun getItemCount(): Int = MEAL.size


}

class DetailHs1ItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(meal: Meal, listener: DetailHs1ItemClickListener) {
        itemView.setOnClickListener {
            listener.onDetailMenuClick(meal)
        }

        with(binding) {
            txtTime.text = meal.mealType
            txtMenu.text =
                meal.mainMenu.replaceCommaToLinebreak() + "\n" + meal.subMenu.replaceCommaToLinebreak()
        }
    }

    companion object {
        fun from(parent: ViewGroup): DetailHs1ItemViewHolder {
            return DetailHs1ItemViewHolder(
                ItemDetailMenuBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}