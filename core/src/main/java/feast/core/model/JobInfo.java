/*
 * Copyright 2018 The Feast Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package feast.core.model;

import com.google.api.Metric;
import feast.core.SourceProto.SourceType;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains information about a run job.
 */
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "jobs")
public class JobInfo extends AbstractTimestampEntity {

  // Internal job name. Generated by feast ingestion upon invocation.
  @Id
  private String id;

  // External job id, generated by the runner and retrieved by feast.
  // Used internally for job management.
  @Column(name = "ext_id")
  private String extId;

  // Runner type
  @Column(name = "runner")
  private String runner;

  // Source id
  @ManyToOne
  @JoinColumn(name = "source_id")
  private Source source;

  // Sink id
  @ManyToOne
  @JoinColumn(name = "store_name")
  private Store store;


  // FeatureSets populated by the job
  @ManyToMany
  @JoinTable(
      joinColumns = {@JoinColumn(name = "job_id")},
      inverseJoinColumns = {@JoinColumn(name = "feature_set_id")})
  private List<FeatureSet> featureSets;

  // Job Metrics
  @OneToMany(mappedBy = "jobInfo", cascade = CascadeType.ALL)
  private List<Metrics> metrics;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 16)
  private JobStatus status;

  public JobInfo() {
    super();
  }

  public JobInfo(String id, String extId, String runner, Source source, Store sink,
      List<FeatureSet> featureSets, JobStatus jobStatus) {
    this.id = id;
    this.extId = extId;
    this.source = source;
    this.runner = runner;
    this.store = sink;
    this.featureSets = featureSets;
    this.status = jobStatus;
  }

  public void updateMetrics(List<Metrics> newMetrics) {
    metrics.clear();
    metrics.addAll(newMetrics);
  }

  public String getSinkName() {
    return store.getName();
  }
}
