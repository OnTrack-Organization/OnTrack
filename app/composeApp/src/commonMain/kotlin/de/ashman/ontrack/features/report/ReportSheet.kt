package de.ashman.ontrack.features.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedTextField
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.network.services.report.dto.ReportReason
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.confirm_button
import ontrack.composeapp.generated.resources.report_additional_info
import ontrack.composeapp.generated.resources.report_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReportSheet(
    isLoading: Boolean,
    onReport: (ReportReason, String) -> Unit
) {
    val reasons = ReportReason.entries

    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }
    var message by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.report_title),
            style = MaterialTheme.typography.titleMedium,
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(reasons) { reason ->
                Row(
                    modifier = Modifier
                        .clickable {
                            selectedReason = reason
                        }
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedReason == reason,
                        onClick = { selectedReason = reason }
                    )

                    Text(
                        text = stringResource(reason.getLabel()),
                    )
                }
            }
        }

        OnTrackOutlinedTextField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = message,
            onValueChange = { message = it },
            placeholder = stringResource(Res.string.report_additional_info),
        )

        OnTrackButton(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            onClick = { selectedReason?.let { onReport(it, message) } },
            enabled = selectedReason != null,
            isLoading = isLoading,
            text = Res.string.confirm_button,
        )
    }
}
