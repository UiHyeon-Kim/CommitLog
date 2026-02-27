package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun StatisticsEmptyView(
    onNavigateToWrite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.InsertChartOutlined,
            contentDescription = stringResource(R.string.stats_empty_content_desc),
            modifier = Modifier.size(80.dp),
            tint = CommitLogTheme.colors.textSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))
        Text(
            text = stringResource(R.string.stats_empty_message),
            style = CommitLogTheme.typography.bodyLarge,
            color = CommitLogTheme.colors.textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
        Button(
            onClick = onNavigateToWrite,
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = Dimensions.SpacingMedium)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Dimensions.SpacingSmall))
            Text(text = stringResource(R.string.stats_empty_button))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun StatisticsEmptyViewPreview() {
    CommitLogTheme {
        StatisticsEmptyView(
            onNavigateToWrite = {}
        )
    }
}
