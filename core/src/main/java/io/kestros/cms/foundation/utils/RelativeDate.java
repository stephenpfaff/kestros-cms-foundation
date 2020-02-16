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

package io.kestros.cms.foundation.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Date wrapper that produces readable elapsed time Strings, i.e 1 day since, 5 hours since, etc.
 */
public class RelativeDate extends Date {

  private static final long serialVersionUID = -2605739752979547091L;

  /**
   * RelativeDate Constructor.
   *
   * @param timestamp Timestamp to converte to RelativeDate.
   */
  public RelativeDate(final Long timestamp) {
    super(timestamp);
  }

  /**
   * Elapsed Time String.  Returns the date as a readable time since String, i.e 1 day since, 5
   * hours since, etc.
   *
   * @return The date as a readable time since String, i.e 1 day since, 5 hours since, etc..
   */
  public String getTimeAgo() {
    final long timeAgo = this.getTime() / 1000;
    final long currentTime = (Calendar.getInstance().getTimeInMillis()) / 1000;
    final long seconds = currentTime - timeAgo;
    if (seconds <= 60) {
      return "Just now";
    } else {
      final long minutes = seconds / 60;

      if (minutes <= 60) {
        if (minutes == 1) {
          return "a minute ago";
        } else {
          return minutes + " minutes ago";
        }
      } else {
        final long hours = seconds / 3600;
        if (hours <= 24) {
          if (hours == 1) {
            return "An hour ago";
          } else {
            return hours + " hrs ago";
          }
        } else {
          final long days = seconds / 86400;
          if (days <= 7) {
            if (days == 1) {
              return "Yesterday";
            } else {
              return days + " days ago";
            }
          } else {
            final long weeks = seconds / 604800;
            if (weeks <= 4.3) {
              if (weeks == 1) {
                return "A week ago";
              } else {
                return weeks + " weeks ago";
              }
            } else {
              final long months = seconds / 2600640;
              if (months <= 12) {
                if (months == 1) {
                  return "A month ago";
                } else {
                  return months + " months ago";
                }
              } else {
                final long years = seconds / 31207680;
                if (years == 1) {
                  return "One year ago";
                } else {
                  return years + " years ago";
                }
              }
            }
          }
        }
      }
    }
  }
}