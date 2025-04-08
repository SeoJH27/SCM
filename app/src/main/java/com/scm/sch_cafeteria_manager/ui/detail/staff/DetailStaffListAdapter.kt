package com.scm.sch_cafeteria_manager.ui.detail.staff

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

class DetailStaffListAdapter(
    items: UserDetailResponse,
    private val dayOfWeek: String
) : RecyclerView.Adapter<DetailStaffItemViewHolder>() {

    private var MEAL: List<meals?> = emptyList()

    init {
        items.data.dailyMeals.forEach {
            if (it.dayOfWeek == dayOfWeek) {
                MEAL = it.meals
                Log.e("DetailStaffListAdapter", "init - $MEAL")
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailStaffItemViewHolder {
        Log.e("DetailStaffListAdapter", "onCreateViewHolder")
        return DetailStaffItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: DetailStaffItemViewHolder, position: Int) {
        if (MEAL.isEmpty()) {
            Log.e("DetailStaffListAdapter",
                "onBindViewHolder: 데이터가 없습니다. 할당: $MEAL")
            holder.emptyBind()
        } else {
            holder.bind(MEAL[position])
        }
    }

    override fun getItemCount(): Int {
        Log.e("DetailStaffListAdapter", "getItemCount")
        return if (MEAL.isEmpty()) 1 else MEAL.size
    }
}

class DetailStaffItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(meal: meals?) {
        Log.e("DetailStaffListAdapter", "bind")
        with(binding) {
            txtTime.text = mealTypeToKorean(meal!!.mealType)
            txtMenu.text = combinMainAndSub(meal.mainMenu, meal.subMenu) ?: nonData
        }
    }

    fun emptyBind() {
        Log.e("DetailStaffListAdapter", "emptyBind")
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
        fun from(parent: ViewGroup): DetailStaffItemViewHolder {
            Log.e("DetailHs1ListAdapter", "from")
            return DetailStaffItemViewHolder(
                ItemDetailMenuBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}