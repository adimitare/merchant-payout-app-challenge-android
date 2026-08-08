package com.example.androidinterview.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.util.formatMoney

@Composable
fun RecentActivityItem(
    activityItem: ActivityItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = activityItem.description,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = formatMoney(activityItem.amount, activityItem.currency),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if(activityItem.amount >= 0)
                Color(color = 0xFF16B955)
            else
                MaterialTheme.colorScheme.error
        )
    }
}
