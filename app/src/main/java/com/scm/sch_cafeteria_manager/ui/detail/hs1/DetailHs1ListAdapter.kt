package com.scm.sch_cafeteria_manager.ui.detail.hs1

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.UserDetailResponse
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.util.utilAll.combinMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.emptyMEAL
import com.scm.sch_cafeteria_manager.util.utilAll.mealTypeToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.nonData
import java.util.Objects.isNull

class DetailHs1ListAdapter(
    items: UserDetailResponse, private val dayOfWeek: String
) : RecyclerView.Adapter<DetailHs1ItemViewHolder>() {

    private var MEAL: List<meals?> = emptyList()

    init {
        items.data.dailyMeals.forEach {
            if (it.dayOfWeek == dayOfWeek) {
                MEAL = it.meals.ifEmpty { emptyMEAL }
                Log.e("DetailHs1ListAdapter", "init - $MEAL")
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHs1ItemViewHolder {
        Log.e("DetailHs1ListAdapter", "onCreateViewHolder")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            Log.e("DetailHs1ListAdapter", "onCreateViewHolder: 데이터가 없습니다. 할당: $MEAL")
        }
        return DetailHs1ItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: DetailHs1ItemViewHolder, position: Int) {
        Log.e("DetailHs1ListAdapter", "onBindViewHolder: ${MEAL.get(position)}")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            Log.e("DetailHs1ListAdapter", "onBindViewHolder: 데이터가 없습니다. 할당: $MEAL")
            holder.bind(MEAL.get(position))
        } else {
            holder.bind(MEAL.get(position))
        }
    }

    override fun getItemCount(): Int {
        Log.e("DetailHs1ListAdapter", "getItemCount")
        return if (MEAL.isEmpty()) 1 else MEAL.size
    }
}

class DetailHs1ItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(meal: meals?) {
        Log.e("DetailHs1ListAdapter", "bind")

        with(binding) {
            if (meal?.mealType != null) {
                txtTime.text = mealTypeToKorean(meal.mealType)
            } else {
                txtTime.text = nonData
                txtTime.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.grey_300
                    )
                )
            }
            val menu = combinMainAndSub(meal?.mainMenu, meal?.subMenu) ?: nonData
            if (isNull(menu)) {
                txtMenu.text = nonData
                txtMenu.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.grey_300
                    )
                )
            } else
                txtMenu.text = menu
        }
    }

    companion object {
        fun from(parent: ViewGroup): DetailHs1ItemViewHolder {
            Log.e("DetailHs1ListAdapter", "from")
            return DetailHs1ItemViewHolder(
                ItemDetailMenuBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}