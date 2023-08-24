package com.example.mystories

import com.example.mystories.api.ListStoryItem

object DataDummy {

    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "createdAt $i",
                "name $i",
                "description $i",
                "id $i",
            )
            items.add(quote)
        }
        return items
    }
}