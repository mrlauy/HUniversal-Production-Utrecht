/*
 *  Copyright [2006-2007] [Stefan Kleine Stegemann]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.openbbs.blackboard.persistence.prevalence;

import java.io.Serializable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.openbbs.blackboard.Zone;

class StoreEntryCommand implements PrevalenceCommand
{
   private final Zone zone;
   private final Object entry;

   public StoreEntryCommand(Zone zone, Object entry) {
      Validate.notNull(zone, "cannot store entry for null zone");
      Validate.notNull(entry, "cannot store null entry");
      Validate.isTrue(entry instanceof Serializable, "entry is not serializable");
      this.zone = zone;
      this.entry = entry;
   }

   public String toString() {
      return new ToStringBuilder(this).append("zone", zone).append("entry", entry).toString();
   }

   public void playback(PlaybackDelegate playbackDelegate) {
      Validate.notNull(playbackDelegate);
      playbackDelegate.storeEntry(this.zone, this.entry);
   }

   private static final long serialVersionUID = 5264242562029115738L;
}