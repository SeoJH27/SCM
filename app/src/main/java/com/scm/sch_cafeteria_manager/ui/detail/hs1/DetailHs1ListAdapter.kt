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
import com.scm.sch_cafeteria_manager.util.utilAll.combineMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.mealTypeToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.nonData

class DetailHs1ListAdapter(
    items: UserDetailResponse,
    private val dayOfWeek: String
) : RecyclerView.Adapter<DetailHs1ItemViewHolder>() {
    private var MEAL: List<meals?> = emptyList()

    init {
        items.data.dailyMeals.forEach {
            if (it.dayOfWeek == dayOfWeek) {
                MEAL = it.meals
                Log.e("DetailHs1ListAdapter", "init - $MEAL")
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHs1ItemViewHolder {
        Log.e("DetailHs1ListAdapter", "onCreateViewHolder")
        return DetailHs1ItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: DetailHs1ItemViewHolder, position: Int) {
        if (MEAL.isEmpty()) {
            Log.e("DetailHs1ListAdapter",
                "onBindViewHolder: 데이터가 없습니다. 할당: $MEAL")
            holder.emptyBind()
        } else {
            holder.bind(MEAL[position])
        }
    }

    override fun getItemCount(): Int {
        Log.e("DetailHs1ListAdapter", "getItemCount: ${MEAL.size}")
        return if (MEAL.isEmpty()) 1 else MEAL.size
    }
}

class DetailHs1ItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(meal: meals?) {
        Log.e("DetailHs1ListAdapter", "bind")
        with(binding) {
            txtTime.text = mealTypeToKorean(meal!!.mealType)
            txtMenu.text = combineMainAndSub(meal.mainMenu, meal.subMenu) ?: nonData
        }
    }

    fun emptyBind() {
        Log.e("DetailHs1ListAdapter", "emptyBind")
        with(binding) {
            txtMenu.text = nonData
            txtMenu.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.grey_300
                )
            )
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