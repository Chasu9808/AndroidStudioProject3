package com.sylovestp.firebasetest.testspringrestapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class UserPagingSource(
    private val apiService: INetworkService
) : PagingSource<Int, UserItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserItem> {
        return try {

            val currentPage = params.key ?: 0

            val response = apiService.getItems2(currentPage, 10)

            if (response.isSuccessful) {
                val data = response.body()?.content ?: emptyList()
                val nextPage = if (data.isNotEmpty()) currentPage + 1 else null

                LoadResult.Page(
                    data = data,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
