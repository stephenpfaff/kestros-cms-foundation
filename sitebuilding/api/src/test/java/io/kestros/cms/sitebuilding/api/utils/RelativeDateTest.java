/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.cms.sitebuilding.api.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class RelativeDateTest {

  private RelativeDate relativeDate;


  @Test
  public void getTimeAgoWhenNow() {
    relativeDate = new RelativeDate(new Date().getTime());

    assertEquals("Just now", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenThirtySecondsAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)).getTime());

    assertEquals("Just now", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenOneMinuteAgo() {
    relativeDate = new RelativeDate(new Date(
        System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2) - -TimeUnit.SECONDS.toMillis(
            30)).getTime());

    assertEquals("a minute ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenThirtyMinutesAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)).getTime());

    assertEquals("30 minutes ago", relativeDate.getTimeAgo());
  }


  @Test
  public void getTimeAgoWhenWhenAnHourAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(59)).getTime());

    assertEquals("59 minutes ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(60)).getTime());

    assertEquals("60 minutes ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(61)).getTime());

    assertEquals("An hour ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenThreeHoursAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3)).getTime());

    assertEquals("3 hrs ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenADayAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(23)).getTime());

    assertEquals("23 hrs ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)).getTime());

    assertEquals("24 hrs ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(25)).getTime());

    assertEquals("Yesterday", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenASeveralDaysAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)).getTime());

    assertEquals("24 hrs ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)).getTime());

    assertEquals("2 days ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)).getTime());

    assertEquals("3 days ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenAWeekAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(6)).getTime());

    assertEquals("6 days ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)).getTime());

    assertEquals("7 days ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8)).getTime());

    assertEquals("A week ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenSeveralWeeksAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(21)).getTime());

    assertEquals("3 weeks ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenAMonthAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(33)).getTime());

    assertEquals("4 weeks ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(34)).getTime());

    assertEquals("4 weeks ago", relativeDate.getTimeAgo());

    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(35)).getTime());

    assertEquals("A month ago", relativeDate.getTimeAgo());

  }

  @Test
  public void getTimeAgoWhenWhenSeveralMonthsAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(100)).getTime());

    assertEquals("3 months ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenAYearAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(400)).getTime());

    assertEquals("One year ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenWhenSeveralYearsAgo() {
    relativeDate = new RelativeDate(
        new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1000)).getTime());

    assertEquals("2 years ago", relativeDate.getTimeAgo());
  }

  @Test
  public void getTimeAgoWhenRoundableMilliseconds() {
    relativeDate = new RelativeDate(new Date(System.currentTimeMillis() - 427389).getTime());

    assertEquals("7 minutes ago", relativeDate.getTimeAgo());
  }
}