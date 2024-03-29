package com.example.commutual.ui.screens.edit_task

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.commutual.FormatterClass
import com.example.commutual.R
import com.example.commutual.common.composable.*
import com.example.commutual.common.ext.fieldModifier
import com.example.commutual.common.ext.spacer
import com.example.commutual.model.CategoryEnum
import com.example.commutual.model.Task
import java.util.*
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction9
import com.example.commutual.R.drawable as AppIcon

@ExperimentalMaterialApi
@Composable
fun EditTaskScreen(
    popUpScreen: () -> Unit,
    taskId: String,
    chatId: String,
    screenTitle: String,
    modifier: Modifier = Modifier,
    viewModel: EditTaskViewModel = hiltViewModel(),
    setAlarmManager: KFunction9<Context, String, String, String, Calendar, Calendar, Array<String>, Task, String, Unit>
) {
    val task by viewModel.task

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val categories = enumValues<CategoryEnum>()
        .filter { (it != CategoryEnum.ANY) && (it != CategoryEnum.NONE) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (viewModel.expandedDropDownMenu)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.initialize(taskId, chatId) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        ActionToolbar(
            title = screenTitle,
            endActionIcon = AppIcon.ic_check
        ) {
            viewModel.onDoneClick(
                chatId,
                popUpScreen,
                focusManager,
                context,
                setAlarmManager
            )
        }

        Spacer(modifier = Modifier.spacer())

        val fieldModifier = Modifier.fieldModifier()

        BasicField(
            R.string.task_title,
            task.title,
            viewModel::onTitleChange, fieldModifier,
            ImeAction.Next,
            KeyboardCapitalization.Words,
            focusManager
        )
        DescriptionField(
            R.string.task_details,
            task.details,
            viewModel::onDetailsChange,
            fieldModifier,
            KeyboardCapitalization.Sentences,
            focusManager
        )

        Column(Modifier.padding(16.dp, 0.dp)) {

            DropDownField(
                value = stringResource(task.category.categoryStringRes),
                onValueChange = {},
                labelText = stringResource(R.string.category),
                icon = icon,
                expanded = viewModel.expandedDropDownMenu,
                setExpandedDropDownMenu = { viewModel.setExpandedDropDownMenu(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    }
            )

            DropdownMenu(
                expanded = viewModel.expandedDropDownMenu,
                onDismissRequest = { viewModel.setExpandedDropDownMenu(false) },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.onCategoryButtonClick(category)
                            viewModel.setExpandedDropDownMenu(false)
                        },
                        text = {
                            Text(
                                text = stringResource(category.categoryStringRes),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            CardEditors(
                startTime = viewModel.startTime,
                endTime = viewModel.endTime,
                year = viewModel.year,
                month = viewModel.month,
                day = viewModel.day,
                viewModel::showDatePicker,
                viewModel::showTimePicker,
                viewModel::onStartTimeChange,
                viewModel::onEndTimeChange
            )


        }

    }

}

@ExperimentalMaterialApi
@Composable
fun CardEditors(
    startTime: String,
    endTime: String,
    year: Int,
    month: Int,
    day: Int,
    showDatePicker: KFunction1<Context, Unit>,
    showTimePicker: (AppCompatActivity?, (Int, Int) -> Unit) -> Unit,
    onStartTimeChange: (Int, Int) -> Unit,
    onEndTimeChange: (Int, Int) -> Unit
) {
    val activity = LocalContext.current as AppCompatActivity
    val context = LocalContext.current

    RegularCardEditor(
        R.string.date,
        R.drawable.ic_calendar,
        FormatterClass.formatDate(selectedYear = year,
            selectedMonth = month,
            selectedDay = day
        )
    ) {
        showDatePicker(context)
    }

    RegularCardEditor(R.string.start_time, R.drawable.ic_time, startTime) {
        showTimePicker(activity, onStartTimeChange)
    }

    RegularCardEditor(R.string.end_time, R.drawable.ic_time, endTime) {
        showTimePicker(activity, onEndTimeChange)
    }
}

