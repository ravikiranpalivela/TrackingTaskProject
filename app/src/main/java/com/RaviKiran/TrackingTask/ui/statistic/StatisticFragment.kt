@file:SuppressLint("StaticFieldLeak")
@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package com.RaviKiran.TrackingTask.ui.statistic

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.RaviKiran.TrackingTask.R
import com.RaviKiran.TrackingTask.extension.onClick
import com.RaviKiran.TrackingTask.extension.toLiveData
import com.RaviKiran.TrackingTask.fit.usecase.GetFitDataUseCase
import com.RaviKiran.TrackingTask.ui.base.BaseFragment
import com.RaviKiran.TrackingTask.ui.base.BaseViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class StatisticFragment : BaseFragment<StatisticViewModel>(R.layout.fragment_statistic) {

    override val viewModel: StatisticViewModel by viewModel()

    // TODO: Replace with ViewBinding
    private inline val tvCurrentDate: TextView get() = requireView().findViewById(R.id.tvCurrentDate)
    private inline val tvPreviousCategory: TextView get() = requireView().findViewById(R.id.tvPreviousCategory)
    private inline val tvCurrentCategory: TextView get() = requireView().findViewById(R.id.tvCurrentCategory)
    private inline val tvNextCategory: TextView get() = requireView().findViewById(R.id.tvNextCategory)
    private inline val btnPreviousCategory: ImageView get() = requireView().findViewById(R.id.btnPreviousCategory)
    private inline val btnNextCategory: ImageView get() = requireView().findViewById(R.id.btnNextCategory)
    private inline val dayB: TextView get() = requireView().findViewById(R.id.dayB)
    private inline val weekB: TextView get() = requireView().findViewById(R.id.weekB)
    private inline val monthB: TextView get() = requireView().findViewById(R.id.monthB)
    private inline val btnPreviousDate: ImageView get() = requireView().findViewById(R.id.btnPreviousDate)
    private inline val btnNextDate: ImageView get() = requireView().findViewById(R.id.btnNextDate)
    private inline val chart: BarChart get() = requireView().findViewById(R.id.chart)

    override fun onReady() {
        viewModel.previousCategoryTitle.observe {
            tvPreviousCategory.text = it
            btnPreviousCategory.isInvisible = it == null
        }
        viewModel.currentCategoryTitle.observe { tvCurrentCategory.text = it }
        viewModel.nextCategoryTitle.observe {
            tvNextCategory.text = it
            btnNextCategory.isInvisible = it == null
        }
        tvPreviousCategory.onClick { viewModel.onPreviousCategoryClicked() }
        btnPreviousCategory.onClick { viewModel.onPreviousCategoryClicked() }
        tvNextCategory.onClick { viewModel.onNextCategoryClicked() }
        btnNextCategory.onClick { viewModel.onNextCategoryClicked() }

        viewModel.isWeekStyleData.observe { isWeekStyle ->
            dayB.updateTabSelection(isWeekStyle == 1)
            weekB.updateTabSelection(isWeekStyle == 2)
            monthB.updateTabSelection(isWeekStyle == 3)
        }
        dayB.onClick { viewModel.onDayStyleSelected() }
        weekB.onClick { viewModel.onWeekStyleSelected() }
        monthB.onClick { viewModel.onMonthStyleSelected() }

        viewModel.currentDateTitle.observe { tvCurrentDate.text = it }
        btnPreviousDate.onClick { viewModel.onPreviousDateRangeClicked() }
        btnNextDate.onClick { viewModel.onNextDateRangeClicked() }

        initChart()
        viewModel.chartData.observe(::displayChartData)
    }

    private fun initChart() {
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(60)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.xAxis.apply {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
        }
        chart.axisLeft.apply {
            setPosition(YAxisLabelPosition.OUTSIDE_CHART)
            spaceTop = 15f
            axisMinimum = 0f
        }
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
    }

    private fun displayChartData(chartData: ChartData) {
        chart.xAxis.valueFormatter = chartData.xFormatter
        chart.axisLeft.valueFormatter = chartData.yFormatter
        chart.axisRight.isEnabled = false

        chart.data = BarData(BarDataSet(chartData.data, ""))
        chart.data.setValueFormatter(chartData.yFormatter)
        chart.invalidate()
    }
}

class StatisticViewModel(
    private val context: Context,
    useCase: GetFitDataUseCase,
) : BaseViewModel() {

    private val categories = listOf(
        Category.ACTIVITY,
        Category.STEPS,
        Category.SLEEP,
    )
    private var currentCategoryIndex: Int = -1
        set(value) {
            field = value
            updateCategories()
        }
    private val currentCategoryData = MutableLiveData(Category.STEPS)
    val previousCategoryTitle = MutableLiveData<String?>()
    val currentCategoryTitle: LiveData<String> = currentCategoryData.map { it.title }
    val nextCategoryTitle = MutableLiveData<String?>()

    val isWeekStyleData = MutableLiveData(1)

    private val calendar = Calendar.getInstance()
    private val currentDate = MutableLiveData<Date>()
    val currentDateTitle = currentDate.switchMap { date ->
        calendar.time = date

        isWeekStyleData.map { isWeekStyle ->
            if (isWeekStyle == 1) {
                val (startTime, endTime) = getDayDates(date)
                calendar.time = startTime
                val from = calendar.get(Calendar.DAY_OF_MONTH)
                val fromMonth = calendar.getDisplayName(
                    Calendar.MONTH, Calendar.SHORT, Locale.getDefault()
                )

//                calendar.time = endTime
//                val to = calendar.get(Calendar.DAY_OF_MONTH)
//                val toMonth = calendar.getDisplayName(
//                    Calendar.MONTH, Calendar.SHORT, Locale.getDefault()
//                )

//                if (fromMonth == toMonth) {
//                    "$from - $to $fromMonth"
//                } else {
                    "$from $fromMonth"
//                +
//                            " - $to $toMonth"
//                }


            }
            else if (isWeekStyle == 2) {
                val (startTime, endTime) = getWeekDates(date)
                calendar.time = startTime
                val from = calendar.get(Calendar.DAY_OF_MONTH)
                val fromMonth = calendar.getDisplayName(
                    Calendar.MONTH, Calendar.SHORT, Locale.getDefault()
                )

                calendar.time = endTime
                val to = calendar.get(Calendar.DAY_OF_MONTH)
                val toMonth = calendar.getDisplayName(
                    Calendar.MONTH, Calendar.SHORT, Locale.getDefault()
                )

                if (fromMonth == toMonth) {
                    "$from - $to $fromMonth"
                } else {
                    "$from $fromMonth - $to $toMonth"
                }
            } else {
                SimpleDateFormat("LLLL", Locale.getDefault()).format(calendar.time)
            }
        }
    }

    val chartData: LiveData<ChartData> = currentCategoryData.switchMap { currentCategory ->
        isWeekStyleData.switchMap { isWeekStyle ->
            currentDate.switchMap { date ->
                val (startTime, endTime) = when (isWeekStyle) {
                    1 -> {
                        getDayDates(date)
                    }
                    2 -> {
                        getWeekDates(date)
                    }
                    else -> {
                        getMonthDates(date)
                    }
                }

                when (currentCategory) {
                    Category.STEPS -> useCase.getSteps(startTime, endTime)
                        .map {
                            it.toChartData(valueFormatter = {
                                it.toString()
                            }) { stepsInfo ->
                                stepsInfo.date to stepsInfo.count
                            }
                        }
                        .toLiveData()
                    else -> {
                        useCase.getSteps(startTime, endTime)
                            .map {
                                it.toChartData(valueFormatter = {
                                    it.toString()
                                }) { stepsInfo ->
                                    stepsInfo.date to stepsInfo.count
                                }
                            }
                            .toLiveData()
                    }
                }
            }
        }
    }

    init {
        currentCategoryIndex = 1
        currentDate.value = Date()
//        useCase.saveSteps()
    }

    // region Category
    fun onPreviousCategoryClicked() = changeCategory(toNextCategory = false)

    fun onNextCategoryClicked() = changeCategory(toNextCategory = true)

    private fun changeCategory(toNextCategory: Boolean) {
        val i = if (toNextCategory) 1 else -1
        val newCategoryIndex = currentCategoryIndex + i
        if (newCategoryIndex < 0 || newCategoryIndex > categories.size) return
        currentCategoryIndex = newCategoryIndex
    }

    private fun updateCategories() {
        previousCategoryTitle.value = categories.getOrNull(currentCategoryIndex - 1)?.title
        currentCategoryData.value = categories[currentCategoryIndex]
        nextCategoryTitle.value = categories.getOrNull(currentCategoryIndex + 1)?.title
    }
    // endregion

    // region Week or Month
    fun onDayStyleSelected() {
        if (isWeekStyleData.value == 1) return
        isWeekStyleData.value = 1
    }
    fun onWeekStyleSelected() {
        if (isWeekStyleData.value == 2) return
        isWeekStyleData.value = 2
    }

    fun onMonthStyleSelected() {
        if (isWeekStyleData.value == 3) return
        isWeekStyleData.value = 3
    }
    // endregion

    // region Date range
    fun onPreviousDateRangeClicked() = changeDateRange(toNextDateRange = false)

    fun onNextDateRangeClicked() = changeDateRange(toNextDateRange = true)

    private fun changeDateRange(toNextDateRange: Boolean) {
        calendar.time = currentDate.value ?: Date()
        val i = if (toNextDateRange) 1 else -1
        when (isWeekStyleData.value) {
            1 -> {
                calendar.add(Calendar.DAY_OF_MONTH, i)
            }
            2 -> {
                calendar.add(Calendar.WEEK_OF_YEAR, i)
            }
            else -> {
                calendar.add(Calendar.MONTH, i)
            }
        }
        currentDate.value = calendar.time
    }

    private fun getDayDates(date: Date): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            time = date
        }.time
        return startDate to startDate
    }

    private fun getWeekDates(date: Date): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        }.time
        val endDate = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.WEEK_OF_YEAR, 1)
            add(Calendar.DAY_OF_WEEK, -1)
            set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
        }.time
        return startDate to endDate
    }

    private fun getMonthDates(date: Date): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, getMinimum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        }.time
        val endDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, getMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
        }.time
        return startDate to endDate
    }
    // endregion

    // region Mapper
    private val Category.title: String
        get() = context.getString(titleResId).lowercase(Locale.getDefault())

    private fun <T> List<T>.toChartData(
        valueFormatter: (Int) -> String,
        mapper: (T) -> Pair<Date, Int>
    ): ChartData {
        val data = mapIndexed { index, t ->
            val value = mapper(t).second
            BarEntry(index.toFloat(), value.toFloat())
        }

        val dateFormatter = SimpleDateFormat("EEE d", Locale.ROOT)

        return ChartData(
            data = data,
            xFormatter = object : IndexAxisValueFormatter(map {
                val date = mapper(it).first
                dateFormatter.format(date)
            }) {
                override fun getFormattedValue(value: Float): String {
                    return values.getOrNull(value.toInt()) ?: ""
                }
            },
            yFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return valueFormatter(value.roundToInt())
                }
            }
        )
        // endregion
    }
}

data class ChartData(
    val data: List<BarEntry>,
    val xFormatter: ValueFormatter,
    val yFormatter: ValueFormatter
)

private enum class Category(@StringRes val titleResId: Int) {
    ACTIVITY(R.string.statistic_category_activity),
    STEPS(R.string.statistic_category_steps),
    SLEEP(R.string.statistic_category_sleep),
}
