package com.example.androidinterview.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.androidinterview.domain.model.Activity


@Composable
fun RecentActivityItem(
    activity: Activity
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Text(
            text = activity.description,
            style = MaterialTheme.typography.bodyLarge
        )


        Text(
            text = formatAmount(activity.amount),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if(activity.amount >= 0)
                Color(0xFF16B955)
            else
                MaterialTheme.colorScheme.error
        )
    }
}

private fun formatAmount(
    amount: Int
): String {
    val pounds = amount / 100.0
    return if(amount >= 0) {
        "£%,.2f".format(pounds)
    } else {
        "-£%,.2f".format(-pounds)
    }
}