package com.criteo.publisher.headerbidding

import com.criteo.publisher.BidManager
import com.criteo.publisher.model.AdUnit
import com.criteo.publisher.model.Slot
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HeaderBiddingTest {

  @Mock
  private lateinit var bidManager: BidManager

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
  }

  @Test
  fun enrichBid_GivenNullObject_DoNothing() {
    val handler = mock<HeaderBiddingHandler>()
    val headerBidding = HeaderBidding(bidManager, listOf(handler))

    headerBidding.enrichBid(null, mock())

    verifyZeroInteractions(bidManager)
    verifyZeroInteractions(handler)
  }

  @Test
  fun enrichBid_GivenHandlerAcceptingObjectButNoBid_Return() {
    val obj = mock<Any>()
    val adUnit = mock<AdUnit>()
    val handler = givenHandler(obj, true)

    bidManager.stub {
      on { getBidForAdUnitAndPrefetch(adUnit) } doReturn null
    }

    val headerBidding = HeaderBidding(bidManager, listOf(handler))

    headerBidding.enrichBid(obj, adUnit)

    verify(handler, never()).enrichBid(any(), anyOrNull(), any())
  }

  @Test
  fun enrichBid_GivenManyHandlerAndBid_EnrichWithFirstAcceptingHandler() {
    val obj = mock<Any>()
    val adUnit = mock<AdUnit>()
    val slot = mock<Slot>()
    val handler1 = givenHandler(obj, false)
    val handler2 = givenHandler(obj, true)
    val handler3 = givenHandler(obj, true)

    bidManager.stub {
      on { getBidForAdUnitAndPrefetch(adUnit) } doReturn slot
    }

    val headerBidding = HeaderBidding(bidManager, listOf(handler1, handler2, handler3))

    headerBidding.enrichBid(obj, adUnit)

    verify(handler1, never()).enrichBid(any(), anyOrNull(), any())
    verify(handler2).enrichBid(obj, adUnit, slot)
    verify(handler3, never()).enrichBid(any(), anyOrNull(), any())
  }

  private fun givenHandler(obj: Any, accepting: Boolean): HeaderBiddingHandler {
    return mock() {
      on { canHandle(obj) } doReturn accepting
    }
  }

}